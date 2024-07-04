package com.neu.monitorSys.roleManage.client;

import com.neu.monitorSys.common.DTO.MyResponse;
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
}
