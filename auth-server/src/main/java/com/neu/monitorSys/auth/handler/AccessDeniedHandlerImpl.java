package com.neu.monitorSys.auth.handler;

import com.alibaba.fastjson.JSON;
import com.neu.monitorSys.auth.constants.ResultCode;
import com.neu.monitorSys.entity.DTO.MyResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        {
            response.setContentType("application/json;charset=UTF-8"); // 返回JSON
            response.setStatus(HttpServletResponse.SC_OK);  // 状态码
            response.getWriter().write(JSON.toJSONString(new MyResponse<>(ResultCode.UNAUTHORIZED.getCode(), "无权限访问", null)));
        }

    }
}