package com.neu.monitor_sys.role_manage.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitor_sys.common.constants.UserRedisPrefix;
import com.neu.monitor_sys.common.entity.Permissions;
import com.neu.monitor_sys.common.entity.Roles;
import com.neu.monitor_sys.role_manage.client.UserClient;
import com.neu.monitor_sys.role_manage.config.PermissionUrlConfig;
import com.neu.monitor_sys.role_manage.constants.PermissionRedisPrefix;
import com.neu.monitor_sys.role_manage.mapper.PermissionsMapper;
import com.neu.monitor_sys.role_manage.service.IPermissionsService;
import com.neu.monitor_sys.role_manage.service.IRolesService;
import com.neu.monitor_sys.role_manage.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    private final PermissionUrlConfig permissionUrlConfig;

    @Autowired
    public PermissionsServiceImpl(PermissionUrlConfig permissionUrlConfig) {
        this.permissionUrlConfig = permissionUrlConfig;
    }

    /**
     * 不分层级获取权限列表
     */
    @Override
    public List<Permissions> getAllPermissions() {
        //查询redis缓存
        Object o = redisUtil.get(PermissionRedisPrefix.PERMISSION_LIST_PREFIX);
        if (o != null) {
            return JSONUtil.toList(o.toString(), Permissions.class);
        }
        //从数据库查询全部权限信息
        List<Permissions> permissions = permissionsMapper.selectList(null);
        if (permissions != null) {
            //存入redis缓存，缓存时间为2小时
            redisUtil.set(PermissionRedisPrefix.PERMISSION_LIST_PREFIX, JSONUtil.toJsonStr(permissions), (long) 2 * 60 * 60);
        }
        return permissions;
    }

    /**
     * 根据roleId获取权限列表
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
        List<Object> permissionsCache = redisUtil.lGet(PermissionRedisPrefix.PERMISSION_ROLE_PREFIX + roleId, 0, -1);
        if (permissionsCache != null && !permissionsCache.isEmpty()) {
            return permissionsCache.stream().map(object -> JSONUtil.toBean((String) object, Permissions.class)).toList();
            //
        }
        //如果redis中没有权限信息，则从数据库中获取
        List<Permissions> permissions = permissionsMapper.selectPermissionsByRoleId(roleId);
        //获取权限树
        permissions = getMenuTreeRecursion(permissions);
        //根据roleId缓存权限信息，List
        for (Permissions permission : permissions) {
            redisUtil.lSet(PermissionRedisPrefix.PERMISSION_ROLE_PREFIX + roleId, JSONUtil.toJsonStr(permission));
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
        return roleId.stream().map(this::getPermissionsByRoleId).flatMap(List::stream).distinct().toList();

    }

    @Override
    public List<Permissions> getPermissionsTree() {
        //查询redis缓存
        Object o = redisUtil.get(PermissionRedisPrefix.PERMISSION_TREE_PREFIX);
        if (o != null) {
            return JSONUtil.toList(o.toString(), Permissions.class);
        }
        List<Permissions> permissions = getAllPermissions();
        List<Permissions> menuTreeRecursion = getMenuTreeRecursion(permissions);
        //存入redis缓存，缓存时间为2小时
        redisUtil.set(PermissionRedisPrefix.PERMISSION_TREE_PREFIX, JSONUtil.toJsonStr(menuTreeRecursion), (long) 2 * 60 * 60);
        return menuTreeRecursion;
    }

    @Override
    @Transactional
    public boolean updatePermissions(Integer roleId, Integer[] permissionsId) {
        boolean result;
        //首先判断角色Id是否存在
        Roles role = rolesService.getRoleById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        if (role.getMname().equals("ADMIN")) {
            throw new RuntimeException("管理员权限不可修改");
        }
        try {

            if (permissionsId == null || permissionsId.length == 0) {
                //删除原有权限
                permissionsMapper.deletePermissionsByRoleId(roleId);
                //删除缓存
                extracted(roleId);
                return true;
            }
            //判断权限是否存在
            List<Permissions> permissions = permissionsMapper.selectBatchIds(Arrays.asList(permissionsId));
            if (permissions.size() != permissionsId.length) {
                return false;
            }
            // 验证每个新增的权限是否包含其父权限
            Set<Integer> allPermissionIds = new HashSet<>(Arrays.asList(permissionsId));
            for (Permissions permission : permissions) {
                Integer parentId = permission.getParentId();
                while (parentId != null && parentId != 0) {
                    allPermissionIds.add(parentId);
                    Permissions parentPermission = permissionsMapper.selectById(parentId);
                    parentId = parentPermission != null ? parentPermission.getParentId() : null;
                }
            }

            // 验证每个新增的权限是否满足依赖权限
            for (Permissions permission : permissions) {
                List<String> dependencies = permissionUrlConfig.getDependencies().get(permission.getApi());
                if (dependencies == null) {
                    continue;
                }
                for (String dependency : dependencies) {
                    Permissions dependencyPermission = permissionsMapper.selectOne(new QueryWrapper<Permissions>().eq("api", dependency));
                    if (dependencyPermission != null) {
                        allPermissionIds.add(dependencyPermission.getId());
                    }

                }
            }
            //比较allPermissionIds和permissionsId元素是否相同
            if (allPermissionIds.size() != permissionsId.length) {
                throw new RuntimeException("权限不满足依赖关系");
            }
            //删除原有权限
            permissionsMapper.deletePermissionsByRoleId(roleId);
            //添加新权限，批处理
            permissionsMapper.addPermissions(roleId, permissionsId);
            //删除缓存
            extracted(roleId);

            result = true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    /**
     * 删除缓存
     *
     * @param roleId 角色id
     */
    private void extracted(Integer roleId) {
        redisUtil.del(PermissionRedisPrefix.PERMISSION_ROLE_PREFIX + roleId);
        //删除用户缓存
        //1.先查询属于该角色的用户id
        Set<Object> logIdSet = redisUtil.sGet(UserRedisPrefix.USER_ROLE_PREFIX + roleId);
        if (logIdSet != null && !logIdSet.isEmpty()) {
            //2.删除用户缓存
            logIdSet.forEach(logId -> redisUtil.del(UserRedisPrefix.USER_DETAIL_PREFIX + logId.toString()));
            //3.删除用户角色缓存
            redisUtil.del(UserRedisPrefix.USER_ROLE_PREFIX + roleId);
        }
    }


    //
    public static List<Permissions> getMenuTreeRecursion(List<Permissions> permissions) {


        // 构建根节点列表和子节点Map
        List<Permissions> rootPermissions = new ArrayList<>();
        Map<Integer, List<Permissions>> childrenMap = new HashMap<>();

        // 根据parentId构建子节点Map
        for (Permissions permission : permissions) {
            Integer parentId = permission.getParentId();
            if (parentId == 0) {
                rootPermissions.add(permission);
            } else {
                childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(permission);
            }
        }

        // 递归设置子节点
        for (Permissions rootPermission : rootPermissions) {
            setChildren(rootPermission, childrenMap);
        }

        // 对返回结果集排序，前端要展示第一个菜单项，做重定向
        rootPermissions.sort(Comparator.comparing(Permissions::getOrderNum));
        //将api全部设为null
        rootPermissions.forEach(Permissions::setApiToNullRecursively);
        return rootPermissions;
    }

    // 递归设置子节点
    private static void setChildren(Permissions parent, Map<Integer, List<Permissions>> childrenMap) {
        List<Permissions> children = childrenMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            children.sort(Comparator.comparing(Permissions::getOrderNum));
            parent.setChildSysMenu(children);
            for (Permissions child : children) {
                setChildren(child, childrenMap);
                if (child.getApi() != null && child.getChildSysMenu() != null && !child.getChildSysMenu().isEmpty()) {
                    List<Permissions> childSysMenu = child.getChildSysMenu();
                    childSysMenu.add(new Permissions(child.getId(), child.getPermissionName(), 0, child.getApi(), child.getPermissionDescription(), 0, 0, null));
                    childSysMenu.sort(Comparator.comparing(Permissions::getOrderNum));
                    child.setChildSysMenu(childSysMenu);
                }
            }
        }
    }

}
