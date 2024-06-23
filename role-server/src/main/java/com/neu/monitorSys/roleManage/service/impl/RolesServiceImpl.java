package com.neu.monitorSys.roleManage.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.monitorSys.entity.DTO.MyResponse;
import com.neu.monitorSys.entity.Roles;
import com.neu.monitorSys.roleManage.VO.PermissionRoleMapVO;
import com.neu.monitorSys.roleManage.client.UserClient;
import com.neu.monitorSys.roleManage.mapper.RolesMapper;
import com.neu.monitorSys.roleManage.service.IRolesService;
import com.neu.monitorSys.roleManage.util.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-07
 */
@Service
public class RolesServiceImpl extends ServiceImpl<RolesMapper, Roles> implements IRolesService {
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private RolesMapper rolesMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserClient userClient;

    @Override
    public List selectAllRoles() {
        //查询redis缓存
        Object data = redisUtil.get("roles");
        if (data != null) {
            return JSONUtil.toList(data.toString(), Roles.class);
        }
        //从数据库查询全部角色信息
        List<Roles> roles = rolesMapper.selectList(null);
        if (roles != null) {
            //存入redis缓存，缓存时间为1小时
            redisUtil.set("roles", JSONUtil.toJsonStr(roles), 60 * 60);
            return roles;
        }
        return ListUtil.empty();

    }

    @Override
    @Transactional
    public boolean updateRole(Roles role) {
        //更新角色信息
        int result = rolesMapper.updateById(role);
        if (result > 0) {
            //删除redis缓存
            redisUtil.del("roles");
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean addRole(Roles role) {
        //新增角色
        int result = rolesMapper.insert(role);
        if (result > 0) {
            //删除redis缓存
            redisUtil.del("roles");
            return true;
        }
        return false;
    }

    @Override
    public Roles getRoleById(Integer roleId) {
        //从数据库查询角色信息
        return rolesMapper.selectById(roleId);
    }

    @Override
    public List<String> getRoleListByPermissionUrl(String permissionUrl) {
        List<String> roles = new ArrayList<>();

        // 从数据库查询角色信息
        List<PermissionRoleMapVO> permissionRoleMapList = rolesMapper.getRoleListByPermissionUrl();

        for (PermissionRoleMapVO permissionRoleMap : permissionRoleMapList) {
            // 获取逗号分隔的角色名字符串并拆分成列表
            String roleNamesStr = permissionRoleMap.getRoleNames().get(0);
            List<String> roleNamesList = Arrays.asList(roleNamesStr.split(","));

            // 正则表达式匹配
            Pattern pattern = Pattern.compile(permissionRoleMap.getPermissionUrl());
            Matcher matcher = pattern.matcher(permissionUrl);

            if (matcher.find()) {
                roles.addAll(roleNamesList);
            }
        }

        return roles;
    }

    @Override
    public List<String> getRoleNamesByUserId(String userId) {
        MyResponse<Integer> roleId = userClient.getRoleIdByLogId(userId);
        //从数据库查询用户角色名称列表
        List<Roles> rolesList = rolesMapper.selectList(new QueryWrapper<Roles>().lambda().eq(Roles::getId, roleId.getData()));
        List<String> roleNames = new ArrayList<>();
        for (Roles role : rolesList) {
            roleNames.add(role.getMname());
        }
        return roleNames;
    }

    @Override
    @Transactional
    public void setDefaultRole(Integer userId) {
        // 先判断是否存在默认角色PUBLIC
        List<Roles> roles = rolesMapper.selectList(new QueryWrapper<Roles>().lambda().eq(Roles::getMname, "PUBLIC"));
        Integer roleId;

        if (roles.isEmpty()) {
            // 不存在则新增默认角色PUBLIC
            Roles role = new Roles();
            role.setMname("PUBLIC");
            rolesMapper.insert(role);
            // 重新获取默认角色PUBLIC的id
            roleId = role.getId();
        } else {
            // 获取默认角色PUBLIC的id
            roleId = roles.get(0).getId();
        }

        // 设置用户角色
        try {
            rolesMapper.addUserRole(userId, roleId);
        } catch (Exception e) {
            throw new RuntimeException("分配角色异常"+e.getMessage());
        }
    }

}
