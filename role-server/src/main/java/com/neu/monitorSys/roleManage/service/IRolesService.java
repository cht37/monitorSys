package com.neu.monitorSys.roleManage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neu.monitorSys.roleManage.entity.Roles;

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
}
