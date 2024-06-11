package com.neu.monitorSys.auth.controller;

import com.neu.monitorSys.auth.DTO.AuthRequest;
import com.neu.monitorSys.auth.DTO.MyResponse;
import com.neu.monitorSys.auth.constants.ResultCode;
import com.neu.monitorSys.auth.entity.Member;
import com.neu.monitorSys.auth.filter.SmsAuthenticationToken;
import com.neu.monitorSys.auth.provider.SmsAuthenticationProvider;
import com.neu.monitorSys.auth.service.MemberService;
import com.neu.monitorSys.auth.service.impl.JwtService;
import com.nimbusds.jose.JOSEException;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private MemberService authService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Resource
    private JwtService jwtService;


    @PostMapping(value = "/register")
    public MyResponse<Null> createUser(@RequestBody Member member) {
        member.setLogpwd(passwordEncoder.encode(member.getLogpwd()));
        authService.saveMember(member);
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", null);
    }

    /**
     * 校验token有效性
     *
     * @param token
     * @return
     * @throws ParseException
     * @throws JOSEException
     */
    @GetMapping(value = "/validate")
    public MyResponse<String> validateToken(@RequestParam String token) throws ParseException, JOSEException {
        if (!jwtService.validateToken(token)) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), null, "Token is invalid");
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), null, "Token is valid");
    }

}
