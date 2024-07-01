package com.neu.monitorSys.auth.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.neu.monitorSys.auth.entity.SmsCode;
import com.neu.monitorSys.auth.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {
    @Autowired
    private RedisUtil redisUtil;
    public void sendSmsCode(String mobile) {
        SmsCode smsCode = createSMSCode();
        String code = smsCode.getCode();

        //存入redis
        redisUtil.set("sms:" + mobile, code, 60*5);
        log.info("向手机" + mobile + "发送验证码" + code+"  验证码有效时间5分钟");
    }

     private SmsCode createSMSCode() {
        String code = RandomUtil.randomNumbers(6);
        return new SmsCode(code, 60*5);
    }
}
