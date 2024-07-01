package com.neu.monitorSys.roleManage.client;

import com.neu.monitorSys.common.DTO.MyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("user-server")
public interface UserClient {
     @GetMapping("/api/v1/members/role-id/{logId}")
     MyResponse<List<Integer>> getRoleIdByLogId(@PathVariable("logId") String logId);
}
