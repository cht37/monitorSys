package com.neu.monitorSys.user.client;

import com.neu.monitorSys.common.DTO.MyResponse;
import com.neu.monitorSys.common.entity.Permissions;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "role-server")
public interface RoleClient {
    @GetMapping("/api/v1/roles/{id}")
    MyResponse getRoleById(@PathVariable("id") Integer id);

     @GetMapping("/api/v1/permissions/role/{roleId}")
    MyResponse<List<Permissions>> getPermissionsByRoleId(@PathVariable("roleId") Integer roleId);

    @GetMapping("/api/v1/roles/users")
    MyResponse<List<String>> getLogIdByRoleName(@RequestParam("roleNames") String roleNames);
}
