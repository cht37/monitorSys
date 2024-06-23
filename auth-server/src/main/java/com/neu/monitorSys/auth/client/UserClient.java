package com.neu.monitorSys.auth.client;

import com.neu.monitorSys.entity.DTO.MemberWithRole;
import com.neu.monitorSys.entity.DTO.MyResponse;
import com.neu.monitorSys.entity.Member;
import com.neu.monitorSys.entity.constants.SecurityConstants;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-server")
public interface UserClient {

    @GetMapping("/api/v1/members/by-mobile/{mobile}")
    MyResponse getMemberByMobile(@PathVariable("mobile") String mobile);

    @GetMapping("/api/v1/members/basic/{logId}")
    MyResponse getMember(@PathVariable("logId") String logId);
    /**
     * 新增用户Member
     */
    @PostMapping("/api/v1/members/add")
    MyResponse<Boolean> saveMember(@RequestBody Member member);

    @GetMapping("/api/v1/members/info")
     MyResponse<MemberWithRole> getMemberInfo(@RequestHeader("logId") String logId);

}
