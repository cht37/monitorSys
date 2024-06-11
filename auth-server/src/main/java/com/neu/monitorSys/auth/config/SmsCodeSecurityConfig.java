package com.neu.monitorSys.auth.config;

import com.neu.monitorSys.auth.filter.SmsAuthenticationFilter;
import com.neu.monitorSys.auth.filter.SmsCodeFilter;
import com.neu.monitorSys.auth.handler.LoginAuthenticationFailureHandler;
import com.neu.monitorSys.auth.handler.LoginSuccessHandle;
import com.neu.monitorSys.auth.provider.SmsAuthenticationProvider;
import com.neu.monitorSys.auth.service.impl.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * 短信登录配置类
 *
 */
@Component
public class SmsCodeSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    @Lazy
    private MyUserDetailsService userDetailsService;
    @Autowired
    private SmsCodeFilter smsCodeFilter;
    @Autowired
    private LoginAuthenticationFailureHandler loginAuthenticationFailureHandler;
    @Autowired
    private LoginSuccessHandle loginSuccessHandle;

    @Override
    public void configure(HttpSecurity http) {
        //1、配置过滤器
        SmsAuthenticationFilter smsAuthenticationFilter = new SmsAuthenticationFilter();
        smsAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        //成功和失败的处理器，需要自己处理
        smsAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandle);
        smsAuthenticationFilter.setAuthenticationFailureHandler(loginAuthenticationFailureHandler);

        //2、配置provider
        SmsAuthenticationProvider smsCodeAuthenticationProvider = new SmsAuthenticationProvider();
        smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);
        //3、确定各个过滤器的顺序，验证码的验证过滤器应该在加载用户详细信息之前
         /*http.addFilterBefore(smsCodeValidateFilter,UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(smsCodeAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);*/
        http.addFilterBefore(smsCodeFilter, UsernamePasswordAuthenticationFilter.class);
        http.authenticationProvider(smsCodeAuthenticationProvider).
            addFilterAfter(smsAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);
    }
}
