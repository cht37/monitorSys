package com.neu.monitorSys.auth.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.neu.monitorSys.auth.DTO.MyResponse;
import com.neu.monitorSys.auth.constants.AuthRedisPrefix;
import com.neu.monitorSys.auth.constants.ResultCode;
import com.neu.monitorSys.auth.entity.CustomUserDetails;
import com.neu.monitorSys.auth.entity.Member;
import com.neu.monitorSys.auth.service.impl.JwtService;
import com.neu.monitorSys.auth.utils.RedisUtil;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandle extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetail = BeanUtil.toBean(authentication.getPrincipal(), CustomUserDetails.class);
        Member myUser = userDetail.getMember();
        try {
            //保存token，用户信息到redis
            myUser.setId(null);
            redisUtil.set(AuthRedisPrefix.USER_DETAIL_PREFIX + myUser.getLogid(), JSONUtil.toJsonStr(myUser), 60 * 60 * 2);
            MyResponse<String> myResponse = new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", jwtService.createToken(myUser.getLogid()));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONUtil.toJsonStr(myResponse));

        } catch (JOSEException e) {
            e.printStackTrace();
            redisUtil.del(AuthRedisPrefix.USER_DETAIL_PREFIX + myUser.getLogid());
            redisUtil.del(AuthRedisPrefix.AUTH_PREFIX + myUser.getLogid());
            response.getWriter().write(JSONUtil.toJsonStr(new MyResponse<>(ResultCode.SERVER_ERROR.getCode(), "登录异常", null)));
        }
    }
}
