package com.neu.monitorSys.roleManage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neu.monitorSys.entity.Roles;


import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-07
 */
public interface IRolesService extends IService<Roles> {
    //查询全部角色信息
    List<Roles> selectAllRoles();

    //编辑某角色信息
    boolean updateRole(Roles role);

    //新增角色
    boolean addRole(Roles role);

    //根据id获取角色信息
    Roles getRoleById(Integer roleId);

    //根据权限url获取角色名称
    List<String> getRoleListByPermissionUrl(String permissionUrl);

    //根据用户id获取角色列表
    List<String> getRoleNamesByUserId(String userId);

    //设置角色为默认角色（“PUBLIC”）
    void setDefaultRole(Integer userId);


}
