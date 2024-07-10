package com.neu.monitor_sys.auth.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

/**
 * 短信验证码过滤器
 * 拦截短信登录请求
 * 请求方法为POST的时候该过滤器生效
 * attemptAuthentication方法从请求中获取到mobile参数值
 * ，并调用SmsAuthenticationToken的SmsAuthenticationToken(String mobile)构造方法创建了一个SmsAuthenticationToken。
 * 下一步就如流程图中所示的那样，SmsAuthenticationFilter将SmsAuthenticationToken交给AuthenticationManager处理。
 */
public class SmsAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String MOBILE_KEY = "mobile";
    private boolean postOnly = true;

    public SmsAuthenticationFilter() {
        super(new AntPathRequestMatcher("/api/v1/auth/login/mobile", "POST"));
    }
    UserDetailsService userDetailsService;
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        //判断SecurityContextHolder是否已经有用户信息
        if(SecurityContextHolder.getContext().getAuthentication()!=null){
            return SecurityContextHolder.getContext().getAuthentication();
        }

        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not support: " + request.getMethod());
        }

        String mobile = request.getParameter(MOBILE_KEY);

        if (mobile == null) {
            mobile = "";
        }
        SmsAuthenticationToken authRequest = new SmsAuthenticationToken(mobile);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, SmsAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}
