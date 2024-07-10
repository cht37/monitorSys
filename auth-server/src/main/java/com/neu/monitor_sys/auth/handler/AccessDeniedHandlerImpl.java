package com.neu.monitor_sys.auth.handler;

import com.alibaba.fastjson.JSON;
import com.neu.monitor_sys.common.constants.AuthRedisPrefix;
import com.neu.monitor_sys.auth.constants.ResultCode;
import com.neu.monitor_sys.auth.utils.RedisUtil;
import com.neu.monitor_sys.common.DTO.MyResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登录失败的异常处理
 */
@Slf4j
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        //获取当前线程id
        long threadId = Thread.currentThread().getId();
        redisUtil.del(AuthRedisPrefix.USER_AUTHENTICATION_PREFIX + threadId);
        response.setContentType("application/json;charset=UTF-8"); // 返回JSON
        response.setStatus(HttpServletResponse.SC_OK);  // 状态码
        response.getWriter().write(JSON.toJSONString(new MyResponse<>(ResultCode.UNAUTHORIZED.getCode(), "无权限访问", null)));

    }
}