package com.neu.monitorSys.roleManage.controller;


import com.neu.monitorSys.common.DTO.MyResponse;
import com.neu.monitorSys.common.entity.Permissions;
import com.neu.monitorSys.common.constants.ResultCode;
import com.neu.monitorSys.roleManage.service.IPermissionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 权限表前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-19
 */
@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionsController {
    @Autowired
    private IPermissionsService permissionsService;

    /**
     * 根据roleId获取权限列表
     *
     * @param roleId
     * @return
     */
    @GetMapping("/role/{roleId}")
    public MyResponse<List<Permissions>> getPermissionsByRoleId(@PathVariable Integer roleId) {
        List<Permissions> permissions = null;
        try {
            permissions = permissionsService.getPermissionsByRoleId(roleId);
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", permissions);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败" + e.getMessage(), null);
        }
    }

    /**
     * 根据logId获取权限列表
     *
     * @param logId
     * @return
     */
    @GetMapping("/me")
    public MyResponse<List<Permissions>> getPermissionsByLogId(@RequestHeader("logId") String logId) {
        List<Permissions> permissions = null;
        try {
            permissions = permissionsService.getPermissionsByLogId(logId);
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", permissions);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败" + e.getMessage(), null);
        }
    }

    /**
     * 获取权限树
     *
     * @return
     */
    @GetMapping("/tree")
    public MyResponse<List<Permissions>> getPermissionsTree() {
        List<Permissions> permissions = null;
        try {
            permissions = permissionsService.getPermissionsTree();
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "查询成功", permissions);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), "查询失败" + e.getMessage(), null);
        }
    }

    /**
     * 新增角色权限
     * @param roleId 用户id
     * @param permissionsId 权限id数组
     * @return 是否新增成功
     */
    @PostMapping("/{roleId}/add")
    public MyResponse<Boolean> addPermissions(@PathVariable Integer roleId, @RequestBody Integer[] permissionsId) {
         boolean added;
        try {
           added = permissionsService.addPermissions(roleId, permissionsId);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "新增失败" + e.getMessage(), false);
        }
        if (added) {
            return new MyResponse<>(ResultCode.SUCCESS.getCode(), "新增成功", true);
        } else {
            return new MyResponse<>(ResultCode.FAILED.getCode(), "新增失败", false);
        }

    }
}

