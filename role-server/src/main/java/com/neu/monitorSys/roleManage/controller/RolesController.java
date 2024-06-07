package com.neu.monitorSys.roleManage.controller;


import com.neu.monitorSys.roleManage.DTO.MyResponse;
import com.neu.monitorSys.roleManage.constants.ResultCode;
import com.neu.monitorSys.roleManage.entity.Roles;
import com.neu.monitorSys.roleManage.service.IRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-07
 */
@RestController
@RequestMapping("/roles")
public class RolesController {
    @Autowired
    private IRolesService rolesService;

    /**
     * 查询全部角色信息
     * @return
     */
    @GetMapping("/getAllRoles")
    MyResponse<List<Roles>> getAllRoles(){

        try {
            List<Roles> rolesList = rolesService.selectAllRoles();
            if (rolesList != null&&rolesList.size()>0) {
                return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功",rolesList);
            }
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "未查询到角色",rolesList);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "查询失败",null);
        }
    }

    /**
     * 编辑某角色信息
     * @param role
     * @return
     */
    @GetMapping("/updateRole")
    MyResponse<Boolean> updateRole(@RequestBody Roles role){
        boolean result=rolesService.updateRole(role);
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "更新成功", true);
        }
        return new MyResponse<>(ResultCode.FAILED.getCode(), "更新失败",false);
    }

    /**
     * 新增角色
     * @param role
     * @return
     */
    @GetMapping("/addRole")
    MyResponse<Boolean> addRole(@RequestBody Roles role){
        boolean result=rolesService.addRole(role);
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "新增成功", true);
        }
        return new MyResponse<>(ResultCode.FAILED.getCode(), "新增失败",false);
    }
}

