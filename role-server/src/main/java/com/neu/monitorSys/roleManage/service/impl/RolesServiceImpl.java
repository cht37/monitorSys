package com.neu.monitorSys.roleManage.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.monitorSys.roleManage.entity.Roles;
import com.neu.monitorSys.roleManage.mapper.RolesMapper;
import com.neu.monitorSys.roleManage.service.IRolesService;
import com.neu.monitorSys.roleManage.util.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
