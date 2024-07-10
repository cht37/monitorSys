package com.neu.monitor_sys.auth.client;

import com.neu.monitor_sys.common.DTO.MemberWithRole;
import com.neu.monitor_sys.common.DTO.MyResponse;
import com.neu.monitor_sys.common.entity.Member;
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

    @GetMapping("/api/v1/members/is-new/{logId}")
    MyResponse<Boolean> isNewMember(@PathVariable("logId") String logId);

    @PutMapping("/api/v1/members/set-is-new/{logId}")
    MyResponse<Boolean> setIsNew(@PathVariable("logId") String logId);
}
