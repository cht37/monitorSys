package com.neu.monitorSys.roleManage.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.entity.Permissions;
import com.neu.monitorSys.roleManage.client.UserClient;
import com.neu.monitorSys.roleManage.mapper.PermissionsMapper;
import com.neu.monitorSys.roleManage.service.IPermissionsService;
import com.neu.monitorSys.roleManage.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        //先在redis中获取权限信息
        List<Object> permissionsCache = redisUtil.lGet("permissions:" + roleId, 0, -1);
        if (permissionsCache != null && !permissionsCache.isEmpty()) {
            return permissionsCache.stream().map(object -> JSONUtil.toBean((String) object, Permissions.class)).collect(Collectors.toList());
            //
        }
        //如果redis中没有权限信息，则从数据库中获取
        List<Permissions> permissions = permissionsMapper.selectPermissionsByRoleId(roleId);
        //根据roleId缓存权限信息，List
        for (Permissions permission : permissions) {
            redisUtil.lSet("permissions:" + roleId, JSONUtil.toJsonStr(permission));
        }
        return permissions;
    }

    @Override
    public List<Permissions> getPermissionsByLogId(String logId) {
        Integer roleId = userClient.getRoleIdByLogId(logId).getData();
        return getPermissionsByRoleId(roleId);

    }
}
