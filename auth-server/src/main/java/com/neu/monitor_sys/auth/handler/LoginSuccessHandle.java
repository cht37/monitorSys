package com.neu.monitor_sys.auth.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.neu.monitor_sys.auth.client.UserClient;
import com.neu.monitor_sys.common.constants.AuthRedisPrefix;
import com.neu.monitor_sys.auth.constants.ResultCode;
import com.neu.monitor_sys.auth.entity.CustomUserDetails;
import com.neu.monitor_sys.auth.service.impl.JwtService;
import com.neu.monitor_sys.auth.utils.RedisUtil;
import com.neu.monitor_sys.common.DTO.MyResponse;
import com.neu.monitor_sys.common.entity.SysUser;
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
    @Autowired
    private UserClient userClient;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetail = BeanUtil.toBean(authentication.getPrincipal(), CustomUserDetails.class);
        SysUser myUser = userDetail.getSysUser();
        try {
            //保存token，用户信息到redis
            myUser.setId(null);
            String token = jwtService.createToken(myUser.getUserName());
//            Object data = userClient.getMemberInfo(myUser.getUserName()).getData();
//            MemberWithRole member = BeanUtil.toBean(data, MemberWithRole.class);
            //自定义userDetail存入redis,用于token校验
            redisUtil.set(AuthRedisPrefix.USER_CUSTOM_DETAIL_PREFIX + myUser.getUserName(), JSONUtil.toJsonStr(userDetail), (long) 60 * 60 * 2);
            //不需要，远程 user-server 已经存储
//            redisUtil.set(AuthRedisPrefix.USER_ROLE_PREFIX + myUser.getUserName(), JSONUtil.toJsonStr(member), 60 * 60 * 2);
            MyResponse<String> myResponse = new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", token);
             //获取当前线程id
            long threadId = Thread.currentThread().getId();
            redisUtil.del(AuthRedisPrefix.USER_AUTHENTICATION_PREFIX+threadId);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONUtil.toJsonStr(myResponse));

        } catch (JOSEException e) {
            e.printStackTrace();

//            redisUtil.del(AuthRedisPrefix.USER_ROLE_PREFIX + myUser.getUserName());
            redisUtil.del(AuthRedisPrefix.USER_CUSTOM_DETAIL_PREFIX + myUser.getUserName());
            redisUtil.del(AuthRedisPrefix.AUTH_PREFIX + myUser.getUserName());
             long threadId = Thread.currentThread().getId();
            redisUtil.del(AuthRedisPrefix.USER_AUTHENTICATION_PREFIX+threadId);
            response.getWriter().write(JSONUtil.toJsonStr(new MyResponse<>(ResultCode.SERVER_ERROR.getCode(), "登录异常", null)));
        }
    }
}
