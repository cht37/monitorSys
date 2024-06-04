package com.neu.monitorSys.user.service;

import com.neu.monitorSys.user.entity.Roles;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
public interface IRolesService extends IService<Roles> {
    /**
     * 根据角色id查询角色名称
     */
    String getRoleNameByRoleId(Integer roleId);
}
