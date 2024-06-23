package com.neu.monitorSys.auth.filter;

import cn.hutool.json.JSONUtil;
import com.neu.monitorSys.auth.DTO.AuthRequest;
import com.neu.monitorSys.auth.service.impl.MyUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;

@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private boolean postOnly = true;

    public AuthenticationFilter() {
        super(new AntPathRequestMatcher("/api/v1/auth/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {

        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not support: " + request.getMethod());
        }
        //请求body
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        // 获取request body字符串
        String requestBody = sb.toString();
        log.info(requestBody);
        AuthRequest authRequest = JSONUtil.toBean(requestBody, AuthRequest.class);
        String logId = authRequest.getLogId();
        String logPwd = authRequest.getLogPwd();
        if (logId == null) {
            logId = "";
        }
        if (logPwd == null) {
            logPwd = "";
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(logId, logPwd);
        // extract login and password
        return this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);

    }

}