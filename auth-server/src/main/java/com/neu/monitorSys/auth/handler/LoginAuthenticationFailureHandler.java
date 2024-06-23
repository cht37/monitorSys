package com.neu.monitorSys.auth.handler;

import cn.hutool.json.JSONUtil;
import com.neu.monitorSys.auth.constants.AuthRedisPrefix;
import com.neu.monitorSys.auth.utils.RedisUtil;
import com.neu.monitorSys.entity.DTO.MyResponse;
import com.neu.monitorSys.auth.constants.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 短信验证错误处理类
 * 2021年10月21日16:51:27
 */
@Component
public class LoginAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws ServletException {
        String message = e.getMessage();
        try {
             long threadId = Thread.currentThread().getId();
            redisUtil.del(AuthRedisPrefix.USER_AUTHENTICATION_PREFIX+threadId);
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().write(JSONUtil.toJsonStr(new MyResponse<>(ResultCode.UNAUTHORIZED.getCode(), "登录失败", message)));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }
}