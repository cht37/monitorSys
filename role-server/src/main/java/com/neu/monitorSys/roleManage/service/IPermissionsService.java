package com.neu.monitorSys.roleManage.service;

import com.neu.monitorSys.common.entity.Permissions;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-19
 */
public interface IPermissionsService extends IService<Permissions> {
    /**
     * 按照三级结构获取权限
     * @return
     */
    List<List<List<Permissions>>> getPermissionsByLevel();
    /**
     * 根据roleId获取权限列表
     */
    List<Permissions> getPermissionsByRoleId(Integer roleId);

    /**
     * 根据userId获取权限列表
     * @param logId
     * @return
     */
    List<Permissions> getPermissionsByLogId(String logId);


    /**
     * 获取权限树
     */
    List<Permissions> getPermissionsTree();

    /**
     * 新增权限
     * @param roleId 角色id
     * @param permissionsId 权限id
     * @return  是否成功
     */
    boolean addPermissions(Integer roleId,Integer[] permissionsId);

}
