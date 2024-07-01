package com.neu.monitorSys.roleManage.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.common.entity.Permissions;
import com.neu.monitorSys.roleManage.client.UserClient;
import com.neu.monitorSys.roleManage.mapper.PermissionsMapper;
import com.neu.monitorSys.roleManage.service.IPermissionsService;
import com.neu.monitorSys.roleManage.service.IRolesService;
import com.neu.monitorSys.roleManage.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-19
 */
@Service
public class PermissionsServiceImpl extends ServiceImpl<PermissionsMapper, Permissions> implements IPermissionsService {
    @Autowired
    private PermissionsMapper permissionsMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserClient userClient;

    @Autowired
    private IRolesService rolesService;

    /**
     * TODO 按照三级结构获取权限
     */
    public List<List<List<Permissions>>> getPermissionsByLevel() {
        return null;
    }

    /**
     * TODO 根据roleId获取权限列表
     *
     * @param roleId
     * @return
     */
    @Override
    public List<Permissions> getPermissionsByRoleId(Integer roleId) {
        //判断role是否被禁用
        if (!rolesService.isRoleEnabled(roleId)) {
            return ListUtil.empty();
        }
        //先在redis中获取权限信息
        List<Object> permissionsCache = redisUtil.lGet("permissions:" + roleId, 0, -1);
        if (permissionsCache != null && !permissionsCache.isEmpty()) {
            return permissionsCache.stream().map(object -> JSONUtil.toBean((String) object, Permissions.class)).collect(Collectors.toList());
            //
        }
        //如果redis中没有权限信息，则从数据库中获取
        List<Permissions> permissions = permissionsMapper.selectPermissionsByRoleId(roleId);
        //获取权限树
        permissions= getMenuTreeRecursion(permissions);
        //根据roleId缓存权限信息，List
        for (Permissions permission : permissions) {
            redisUtil.lSet("permissions:" + roleId, JSONUtil.toJsonStr(permission));
        }
        return permissions;
    }

    /**
     * 根据logId获取权限列表
     *
     * @param logId 用户id
     * @return 权限列表
     */
    @Override
    public List<Permissions> getPermissionsByLogId(String logId) {
        List<Integer> roleId = userClient.getRoleIdByLogId(logId).getData();
        if (roleId == null || roleId.isEmpty()) {
            return ListUtil.empty();
        }
        return roleId.stream().map(this::getPermissionsByRoleId).flatMap(List::stream).distinct().collect(Collectors.toList());

    }

    @Override
    public List<Permissions> getPermissionsTree() {
        List<Permissions> permissions = permissionsMapper.selectList(null);
        return getMenuTreeRecursion(permissions);
    }

    @Override
    @Transactional
    public boolean addPermissions(Integer roleId, Integer[] permissionsId) {
        boolean result;
        //首先判断角色Id是否存在
        try {
            if (rolesService.getRoleById(roleId) == null) {
                return false;
            }
            //判断权限是否存在
            List<Permissions> permissions = permissionsMapper.selectBatchIds(Arrays.asList(permissionsId));
            if (permissions.size() != permissionsId.length) {
                return false;
            }
            //删除原有权限
            permissionsMapper.deletePermissionsByRoleId(roleId);
            //添加新权限，批处理
            permissionsMapper.addPermissions(roleId, permissionsId);
            //删除缓存
            redisUtil.del("permissions:" + roleId);
            result = true;
        } catch (Exception e) {
            throw new RuntimeException("添加权限失败");
        }
        return result;
    }

    public static List<Permissions> getMenuTreeRecursion(List<Permissions> permissions) {
        /**
         * 过滤分出父级菜单和子级菜单
         */
        List<Permissions> parentSysMenuList = permissions.stream().filter(sysMenu -> sysMenu.getParentId() == 0).collect(Collectors.toList());
        List<Permissions> childSysMenuList = permissions.stream().filter(sysMenu -> sysMenu.getParentId() > 0).toList();
        //将所有api全部设为null
        childSysMenuList.forEach(e -> e.setApi(null));
        parentSysMenuList.forEach(e -> e.setApi(null));
        /**
         * 将子级目录菜单转换为map对象
         */
        Map<Integer, List<Permissions>> map = childSysMenuList.stream().collect(Collectors.toMap(Permissions::getParentId,
                // 此时的value 为集合，方便重复时操作
                s -> {
                    List<Permissions> childSysMenuMap = new ArrayList<>();
                    childSysMenuMap.add(s);
                    return childSysMenuMap;
                },
                // 重复时将现在的值全部加入到之前的值内
                (List<Permissions> value1, List<Permissions> value2) -> {
                    value1.addAll(value2);
                    return value1;
                }
        ));

        /**
         * 循环对比，父级菜单和子级菜单，相同则加入父级对象中
         */
        parentSysMenuList.forEach(e -> {
            List<Permissions> childSysMenus = map.get(e.getId());
            e.setChildSysMenu(childSysMenus);
        });
        /**
         * 需要对返回结果集排序，前端要展示第一个菜单项，做重定向
         */
        parentSysMenuList.sort(Comparator.comparing(Permissions::getOrderNum));
        System.out.println(parentSysMenuList);
        return parentSysMenuList;
    }
}
