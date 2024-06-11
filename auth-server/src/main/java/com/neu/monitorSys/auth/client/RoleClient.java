package com.neu.monitorSys.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("role-server")
public interface RoleClient {

}
