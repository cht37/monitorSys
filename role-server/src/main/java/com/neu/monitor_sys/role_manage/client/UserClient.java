package com.neu.monitor_sys.role_manage.client;

import com.neu.monitor_sys.common.DTO.MyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("user-server")
public interface UserClient {
    @GetMapping("/api/v1/members/role-id/{logId}")
    MyResponse<List<Integer>> getRoleIdByLogId(@PathVariable("logId") String logId);


    @PostMapping("/api/v1/grid-managers")
    MyResponse<Boolean> addGridManager(@RequestParam("logId") String logId);

    @DeleteMapping("/api/v1/grid-managers")
    MyResponse<Boolean> deleteGridManager(@RequestParam("logId") String logId);

    @PutMapping("/api/v1/members/state")
    MyResponse<Boolean> setState(@RequestParam("logId") String logId, @RequestParam("state") Integer state);
}
