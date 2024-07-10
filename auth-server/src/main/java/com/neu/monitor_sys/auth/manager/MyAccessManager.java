package com.neu.monitor_sys.auth.manager;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.json.JSONUtil;
import com.neu.monitor_sys.auth.client.RoleClient;
import com.neu.monitor_sys.common.constants.AuthRedisPrefix;
import com.neu.monitor_sys.auth.entity.CustomUserDetails;
import com.neu.monitor_sys.auth.utils.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 已弃用
 */
@Component
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MyAccessManager implements AuthorizationManager<RequestAuthorizationContext> {
    private Set<String> permitAll = new ConcurrentHashSet<>();
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private RoleClient roleClient;
    private RedisUtil redisUtil;



    public boolean checkAuthority(Authentication authentication, List<String> roles) throws AccessDeniedException, InsufficientAuthenticationException {
        // 遍历用户拥有的权限，与当前请求的 URL 进行匹配
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if(authority.getAuthority().equals("ADMIN")){
                return true;
            }
            log.info("#MyAccessManager#authority.getAuthority()" + authority.getAuthority());
            if (roles.contains(authority.getAuthority())) {
                return true;
            }
        }

        // 如果没有匹配成功，表示没有权限访问，抛出 AccessDeniedException 异常
        return false;
    }

    private boolean permitAll(String requestUrl) {
        for (String url : permitAll) {
            if (antPathMatcher.match(url, requestUrl)) {
                return true;
            }
        }
        return false;
    }


    @SneakyThrows
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        HttpServletRequest request = object.getRequest();

        String originURI = request.getParameter("originURI");
        String uri=request.getRequestURI();

        if (permitAll(uri)) {
            return new AuthorizationDecision(true);
        }
//        //暂时关闭
//        if (originURI!=null){
//            return new AuthorizationDecision(true);
//        }
        if(originURI==null&&uri.equals("/api/v1/auth/validate")){
            return new AuthorizationDecision(false);
        }else if (originURI == null){
            originURI =request.getMethod()+" "+uri;
        }

        //远程调用查看需要的权限
        List<String> roles = roleClient.getRolesByPermissionUrl(originURI).getData();


        if (roles==null){
            roles= ListUtil.empty();
        }

        log.info("#MyAccessManager#requestUrl" + originURI);
        boolean isMatch;
        //获取当前线程id
        long threadId = Thread.currentThread().getId();
        //从redis中读取认证信息
        String authStr = redisUtil.get(AuthRedisPrefix.USER_AUTHENTICATION_PREFIX + threadId).toString();
        if(authStr==null){
            return new AuthorizationDecision(false);
        }
        CustomUserDetails customUserDetails = JSONUtil.toBean(authStr, CustomUserDetails.class,true);
        if (customUserDetails==null){
            return new AuthorizationDecision(false);
        }
        UsernamePasswordAuthenticationToken thisAuthentication=new UsernamePasswordAuthenticationToken(customUserDetails,null,customUserDetails.getAuthorities());

//        try {
//            isMatch = checkAuthority(thisAuthentication, roles);
//        } finally {
//            //清除当前线程认证信息
//            redisUtil.del(AuthRedisPrefix.USER_AUTHENTICATION_PREFIX + threadId);
//        }
         isMatch = checkAuthority(thisAuthentication, roles);
        if (isMatch) {
            return new AuthorizationDecision(true);
        } else {
            throw new AccessDeniedException("无访问权限");
        }

    }

    @Override
    public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        AuthorizationManager.super.verify(authentication, object);
    }



}
