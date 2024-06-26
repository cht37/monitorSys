package com.neu.monitorSys.feedback.client;

import com.neu.monitorSys.common.DTO.MyResponse;
import com.neu.monitorSys.feedback.DTO.AssignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-server")
public interface UserClient {
    @GetMapping("/api/v1/members/name")
    MyResponse getName(@RequestParam("logId") String logId);

    @GetMapping("/api/v1/grid-managers/assignable")
    MyResponse<Boolean> isAssign(@RequestParam("logId") String logId);

    @GetMapping("/api/v1/grid-managers/afId")
    MyResponse<Integer> getAfIdByLogId(@RequestParam("logId") String logId);
}
