package com.neu.monitorSys.gateway;

import com.neu.monitorSys.gateway.entity.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-server")
public interface  FeignAuthClient {
    @GetMapping("/auth/validate")
    public Response<String> validate(@RequestParam String token);
}
