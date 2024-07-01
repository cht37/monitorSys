package com.neu.monitorSys.statistics.client;

import com.neu.monitorSys.common.DTO.MyResponse;
import com.neu.monitorSys.common.entity.GridManager;
import org.apache.ibatis.jdbc.Null;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-server")
public interface UserClient {

        @GetMapping("/api/v1/members/name")
        MyResponse getName(@RequestParam("logId") String logId);

        @PutMapping("/api/v1/grid-managers")
         MyResponse<Null> editGridMember(@RequestBody GridManager gridManager);

        @GetMapping("/api/v1/grid-managers/afId")
        MyResponse<Integer> getAfIdByLogId(@RequestParam("logId") String logId);

}
