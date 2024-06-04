package com.neu.monitorSys.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.user.entity.Roles;
import com.neu.monitorSys.user.mapper.RolesMapper;
import com.neu.monitorSys.user.service.IRolesService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
@Service
public class RolesServiceImpl extends ServiceImpl<RolesMapper, Roles> implements IRolesService {
    @Resource
    private RolesMapper rolesMapper;

    @Override
    public String getRoleNameByRoleId(Integer roleId) {
        return rolesMapper.selectById(roleId).getMname();
    }
}
