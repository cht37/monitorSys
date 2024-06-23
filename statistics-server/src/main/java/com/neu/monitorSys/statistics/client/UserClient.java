package com.neu.monitorSys.statistics.client;

import com.neu.monitorSys.entity.DTO.MyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-server")
public interface UserClient {

        @GetMapping("/api/v1/members/name")
        MyResponse getName(@RequestParam("logId") String logId);

}
