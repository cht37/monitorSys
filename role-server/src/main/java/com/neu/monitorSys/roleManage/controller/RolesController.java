package com.neu.monitorSys.roleManage.controller;


import com.neu.monitorSys.common.DTO.MyResponse;
import com.neu.monitorSys.common.constants.ResultCode;
import com.neu.monitorSys.common.entity.Roles;
import com.neu.monitorSys.roleManage.service.IRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-07
 */
@RestController
@RequestMapping("/api/v1/roles")
public class RolesController {
    @Autowired
    private IRolesService rolesService;

    /**
     * 查询全部角色信息
     * @return
     */
    @GetMapping
    public MyResponse<List<Roles>> getAllRoles(){

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
    @PutMapping
    public MyResponse<Boolean> updateRole(@RequestBody Roles role){
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
    @PostMapping
    public MyResponse<Boolean> addRole(@RequestBody Roles role){
        boolean result=rolesService.addRole(role);
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "新增成功", true);
        }
        return new MyResponse<>(ResultCode.FAILED.getCode(), "新增失败",false);
    }
    /**
     * 根据id获取角色信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")

    public   MyResponse<Roles> getRoleById(@PathVariable Integer id){
        Roles role=rolesService.getRoleById(id);
        if (role!=null) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", role);
        }
        return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "未查询到角色",null);
    }

    /**
     * 根据权限url获取角色名称列表
     * @param permissionUrl
     * @return
     */
    @GetMapping("/permissions")
    public MyResponse<List<String>> getRolesByPermissionUrl(@RequestParam String permissionUrl){
        List<String> roles= null;
        try {
            roles = rolesService.getRoleListByPermissionUrl(permissionUrl);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "查询失败"+e.getMessage(),null);
        }
        if (roles!=null&&roles.size()>0) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", roles);
        }
        return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "未查询到角色",null);
    }

    /**
     * 根据用户id获取角色名称列表
     * @param userId
     * @return
     */

    @GetMapping("/users/{logId}/roles")
    public MyResponse<List<String>> getRoleNamesByUserId(@PathVariable String logId){
        List<String> roles=rolesService.getRoleNamesByUserId(logId);
        if (roles!=null&&roles.size()>0) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", roles);
        }
        return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "未查询到角色",null);
    }


    /**
     * 设置角色为默认角色（“PUBLIC”）
     * @param userId 用户主键
     * @return 是否设置成功
     */
    @PostMapping("/users/{userId}/default")
    public MyResponse<Boolean> setDefaultRole(@PathVariable Integer userId){

        try {
            rolesService.setDefaultRole(userId);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "设置失败",false);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "设置成功",true);
    }


    /**
     * 修改用户角色
     * @param logId 用户id
     * @param roleIds 角色id列表
     * @return 是否修改成功
     */
    @PutMapping("/users/{logId}/roles")
    public MyResponse<Boolean> updateUserRole(@PathVariable String logId, @RequestBody List<Integer> roleIds){
        boolean result;
        try {
            result= rolesService.updateUserRole(logId,roleIds);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "修改失败",false);
        }
        if (result) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "修改成功",true);
        }else {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "修改失败",false);
        }

    }

    /**
     * 根据角色字符串获取用户id列表
     * @param roleNames 角色字符串
     * @return 用户id列表
     */
    @GetMapping("/users")
    public MyResponse<List<String>> getLogIdByRoleName(@RequestParam String roleNames){
        List<String> logIds= null;
        try {
            logIds = rolesService.getLogIdByRoleName(roleNames);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "查询失败"+e.getMessage(),null);
        }
        if (logIds!=null&&logIds.size()>0) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", logIds);
        }
        return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "未查询到用户",null);
    }
}

