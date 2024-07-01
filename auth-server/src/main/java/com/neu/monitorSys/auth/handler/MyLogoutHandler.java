package com.neu.monitorSys.auth.handler;

import com.neu.monitorSys.auth.constants.AuthRedisPrefix;
import com.neu.monitorSys.auth.utils.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class MyLogoutHandler implements LogoutHandler {
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            String logId = request.getHeader("logId");
            String token = request.getHeader("Authorization");
            if (logId == null) {
                throw new RuntimeException("登出失败");
            }
            if (token == null||token.isEmpty()) {
                throw new RuntimeException("非法请求");
            }
            //判断token是否有效
            if (!redisUtil.hasKey(AuthRedisPrefix.AUTH_PREFIX + logId)) {
                throw new RuntimeException("非法请求");
            }
            //清除登录信息
            redisUtil.del(AuthRedisPrefix.USER_CUSTOM_DETAIL_PREFIX + logId);
            //清除用户token
            redisUtil.del(AuthRedisPrefix.AUTH_PREFIX + logId);
            long threadId = Thread.currentThread().getId();
            redisUtil.del(AuthRedisPrefix.USER_AUTHENTICATION_PREFIX+threadId);
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
        }
    }
}
