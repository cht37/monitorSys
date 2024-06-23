package com.neu.monitorSys.auth.filter;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.neu.monitorSys.auth.config.AuthConfig;
import com.neu.monitorSys.auth.constants.AuthRedisPrefix;
import com.neu.monitorSys.auth.entity.CustomUserDetails;
import com.neu.monitorSys.auth.utils.JwtUtil;
import com.neu.monitorSys.auth.utils.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private RedisUtil redisUtil;
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取请求头中的token
        String token = request.getHeader("Authorization");
        log.info(token);
        //解析token
        if (StrUtil.hasEmpty(token)) {
            //放行
            filterChain.doFilter(request, response);
            return;
        }
        String username = null;
        try {
            Map<String, Object> payLoad = JwtUtil.getPayLoad(token);
            username = (String) payLoad.get("logId");
        } catch (ParseException e) {
            throw new RuntimeException("Token is invalid");
        }
        //从redis中获取用户信息
        String key = AuthRedisPrefix.USER_CUSTOM_DETAIL_PREFIX + username;
        if (!redisUtil.hasKey(key)) {
            throw new RuntimeException("服务器异常登录失败");
        }
        String UserDetailJsonStr = redisUtil.get(key).toString();
        CustomUserDetails customUserDetails = JSONUtil.toBean(UserDetailJsonStr, CustomUserDetails.class, true);
        //普通用户，无权限信息
        UsernamePasswordAuthenticationToken authentication = null;
        if (customUserDetails.getAuthorities() == null) {
            authentication =
                    new UsernamePasswordAuthenticationToken(customUserDetails, null, null);
        } else {
            authentication =
                    new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            //认证信息存入redis,键为线程id
            //获取当前线程id
            long threadId = Thread.currentThread().getId();
            redisUtil.set(AuthRedisPrefix.USER_AUTHENTICATION_PREFIX+threadId,JSONUtil.toJsonStr(customUserDetails ));

            System.out.println(SecurityContextHolder.getContext().getAuthentication().toString());
            //放行
            filterChain.doFilter(request, response);
        }
    }
    private boolean isWhitelist(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String filterPath = request.getRequestURI();
        if (ArrayUtil.isEmpty(AuthConfig.whiteList)) {
            return false;
        }
        for (String path : AuthConfig.whiteList) {
            String pattern = contextPath + path;
            pattern = pattern.replaceAll("/+", "/");
            if (pathMatcher.match(pattern, filterPath)) {
                return true;
            }
        }
        return false;
    }

}
