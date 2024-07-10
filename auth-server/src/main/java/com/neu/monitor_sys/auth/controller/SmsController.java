package com.neu.monitor_sys.auth.controller;

import com.neu.monitor_sys.auth.constants.ResultCode;
import com.neu.monitor_sys.auth.service.impl.SmsService;
import com.neu.monitor_sys.common.DTO.MyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/code")
public class SmsController {
    @Autowired
    private SmsService smsService;
    @PostMapping("/sms")
    public MyResponse<Boolean> sendSmsCode(String mobile) {
        try {
            smsService.sendSmsCode(mobile);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.SERVER_ERROR.getCode(), e.getMessage(), false);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "发送成功", true);
    }
}
