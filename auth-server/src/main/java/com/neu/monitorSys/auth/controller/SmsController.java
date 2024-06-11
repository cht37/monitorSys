package com.neu.monitorSys.auth.controller;

import com.neu.monitorSys.auth.service.impl.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SmsController {
    @Autowired
    private SmsService smsService;
    @GetMapping("/code/sms")
    public void sendSmsCode(String mobile) {
        smsService.sendSmsCode(mobile);
    }
}
