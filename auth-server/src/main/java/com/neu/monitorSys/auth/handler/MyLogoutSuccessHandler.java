package com.neu.monitorSys.auth.handler;

import cn.hutool.json.JSONUtil;
import com.neu.monitorSys.common.DTO.MyResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (request.getAttribute("error") != null) {
            MyResponse<String> myResponse = new MyResponse<>(401, "error", (String) request.getAttribute("error"));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONUtil.toJsonStr(myResponse));
            return;
        }
        MyResponse<String> myResponse = new MyResponse<>(200, "success", "退出成功");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(myResponse));
    }
}
