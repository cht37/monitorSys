package com.neu.monitorSys.auth.client;

import com.neu.monitorSys.entity.DTO.MyResponse;
import com.neu.monitorSys.entity.Permissions;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("role-server")
public interface RoleClient {
    @GetMapping("/api/v1/permissions/me")
    MyResponse<List<Permissions>> getPermissionsByLogId(@RequestHeader("logId") String logId);

    @GetMapping("/api/v1/roles/permissions")
    MyResponse<List<String>> getRolesByPermissionUrl(@RequestParam("permissionUrl") String permissionUrl);

    @GetMapping("/api/v1/roles/users/{userId}/roles")
    MyResponse<List<String>> getRoleNamesByUserId(@PathVariable("userId") String userId);

     /**
     * 设置角色为默认角色（“PUBLIC”）
     * @param userId 用户主键
     * @return 是否设置成功
     */
    @PostMapping("/api/v1/roles/users/{userId}/default")
    MyResponse<Boolean> setDefaultRole(@PathVariable("userId") Integer userId);

}
