package com.neu.monitorSys.auth.controller;
import com.neu.monitorSys.auth.entity.AuthRequest;
import com.neu.monitorSys.auth.entity.Member;
import com.neu.monitorSys.auth.entity.Response;
import com.neu.monitorSys.auth.entity.ResultCode;
import com.neu.monitorSys.auth.service.MemberService;
import com.neu.monitorSys.auth.service.impl.JwtService;
import com.nimbusds.jose.JOSEException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping(value = "/register")
    public ResponseEntity createUser(@RequestBody Member member) {
        member.setLogpwd(passwordEncoder.encode(member.getLogpwd()));
        authService.saveMember(member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/token")
    public Response<String> generateToken(@RequestBody AuthRequest authRequest) throws JOSEException {
        final Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getLogId(), authRequest.getLogPwd()));
        if (authenticate.isAuthenticated()) {
            final String token = jwtService.createToken(authRequest.getLogId());
            return new Response<>(ResultCode.SUCCESS.getCode(), token, "success");
        } else {
            return new Response<>(ResultCode.FAILED.getCode(), null, "failed");
        }
    }

    @GetMapping(value = "/validate")
    public Response<String> validateToken(@RequestParam String token) throws ParseException, JOSEException {
        if (!jwtService.validateToken(token)) {
            return new Response<>(ResultCode.FAILED.getCode(), null, "Token is invalid");
        }
        return new Response<>(ResultCode.SUCCESS.getCode(), null, "Token is valid");
    }
}
