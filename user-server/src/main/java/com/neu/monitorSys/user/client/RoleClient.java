package com.neu.monitorSys.user.client;

import com.neu.monitorSys.user.DTO.MyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "role-server")
public interface RoleClient {
    @GetMapping("/roles/getRoleById/{id}")
    MyResponse getRoleById(@PathVariable("id") Integer id);

}
