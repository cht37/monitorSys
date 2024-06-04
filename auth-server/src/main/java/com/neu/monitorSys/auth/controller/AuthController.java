package com.neu.monitorSys.auth.controller;
import com.neu.monitorSys.auth.entity.Member;
import com.neu.monitorSys.auth.service.AuthService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    /**
     * 密码登录
     * @param loginRequest
     * @return
     */
    @PostMapping("/login")
    public String login(@RequestBody Member loginRequest) {
        return authService.login(loginRequest.getLogid(), loginRequest.getLogpwd());
    }

    @PostMapping("/register")
    public String register(@RequestBody Member registerRequest) {
        authService.register(registerRequest);
        return "Register successful";
    }
}