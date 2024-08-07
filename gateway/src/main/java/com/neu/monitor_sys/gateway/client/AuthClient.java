package com.neu.monitor_sys.gateway.client;

import com.neu.monitor_sys.common.DTO.MyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-server")
public interface AuthClient {
    @GetMapping("/api/v1/auth/validate")
    MyResponse<String> validate(@RequestHeader("Authorization") String token, @RequestParam("originURI") String originURI);
}
