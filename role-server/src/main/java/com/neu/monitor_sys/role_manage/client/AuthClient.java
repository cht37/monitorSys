package com.neu.monitor_sys.role_manage.client;

import com.neu.monitor_sys.common.DTO.MyResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-server")
public interface AuthClient {
        @PostMapping("/api/v1/auth/logout")
        MyResponse<String> logout(@RequestHeader("logId")String  logId,@RequestHeader("Authorization")String token);
}