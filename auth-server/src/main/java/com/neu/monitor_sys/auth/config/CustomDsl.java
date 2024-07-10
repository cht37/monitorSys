package com.neu.monitor_sys.auth.config;

import com.neu.monitor_sys.auth.filter.AuthenticationFilter;
import com.neu.monitor_sys.auth.handler.LoginSuccessHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.stereotype.Component;

@Component
public class CustomDsl extends AbstractHttpConfigurer<CustomDsl, HttpSecurity> {
    @Autowired
    private LoginSuccessHandle loginSuccessHandle;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    @Lazy
    private AuthenticationProvider authenticationProvider;
    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http.addFilterAfter(authenticationFilter(authenticationManager), ConcurrentSessionFilter.class);
        http.authenticationProvider(authenticationProvider);
    }

    public AuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) {
        final AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager);
        authenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandle);
        authenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        return authenticationFilter;
    }

}