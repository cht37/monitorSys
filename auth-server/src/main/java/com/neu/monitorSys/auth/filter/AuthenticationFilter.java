package com.neu.monitorSys.auth.filter;

import cn.hutool.json.JSONUtil;
import com.neu.monitorSys.auth.DTO.AuthRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.BufferedReader;
import java.io.IOException;


public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private boolean postOnly = true;

    public AuthenticationFilter() {
        super(new AntPathRequestMatcher("/auth/login", "POST"));
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