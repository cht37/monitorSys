package com.neu.monitor_sys.role_manage.service;

import com.neu.monitor_sys.common.entity.Permissions;
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
     * 不分层级获取权限列表
     * @return 权限列表
     */
    List<Permissions> getAllPermissions();
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
     * 修改角色权限
     * @param roleId 角色id
     * @param permissionsId 权限id
     * @return  是否成功
     */
    boolean updatePermissions(Integer roleId, Integer[] permissionsId);

//    /**
//     * 获取角色权限列表（树）
//     * @param roleId 角色id
//     * @return 权限列表
//     */
//    List<Permissions> getPermissionsTreeByRoleId(Integer roleId);


}
