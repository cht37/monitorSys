package com.neu.monitorSys.auth.handler;

import cn.hutool.json.JSONUtil;
import com.neu.monitorSys.auth.constants.AuthRedisPrefix;
import com.neu.monitorSys.auth.utils.RedisUtil;
import com.neu.monitorSys.common.DTO.MemberWithRole;
import com.neu.monitorSys.common.constants.UserRedisPrefix;
import com.neu.monitorSys.common.entity.Roles;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.List;

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
            //获取用户角色列表
            Object o = redisUtil.get(UserRedisPrefix.USER_DETAIL_PREFIX + logId);

            List<Roles> roles = null;
            if (o!=null) {
                MemberWithRole memberWithRole = JSONUtil.toBean(o.toString(), MemberWithRole.class);
                roles = memberWithRole.getRoles();
            }
            //清除登录信息
            redisUtil.del(AuthRedisPrefix.USER_CUSTOM_DETAIL_PREFIX + logId);
            //清除用户token
            redisUtil.del(AuthRedisPrefix.AUTH_PREFIX + logId);
            long threadId = Thread.currentThread().getId();
            redisUtil.del(AuthRedisPrefix.USER_AUTHENTICATION_PREFIX+threadId);
            //清除角色用户索引
            if (roles != null) {
                for (Roles role : roles) {
                    redisUtil.setRemove(UserRedisPrefix.USER_ROLE_PREFIX + role.getId(),logId);
                }
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
        }
    }
}
