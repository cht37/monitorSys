package com.neu.monitorSys.auth.client;

import com.neu.monitorSys.auth.DTO.MyResponse;
import com.neu.monitorSys.auth.entity.Member;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-server")
public interface UserClient {
    @GetMapping("/member/getMember/{logId}")
    MyResponse getMember(@PathVariable("logId") String logId, @RequestParam(name = "method",required = false) String method);

    @GetMapping("/member/getMemberByMobile/{mobile}")
    MyResponse getMemberByMobile(@PathVariable("mobile") String mobile);
}
