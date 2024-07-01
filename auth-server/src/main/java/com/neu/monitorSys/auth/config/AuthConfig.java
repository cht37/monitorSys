package com.neu.monitorSys.auth.config;

import cn.hutool.json.JSONUtil;
import com.neu.monitorSys.auth.client.RoleClient;
import com.neu.monitorSys.auth.filter.JwtAuthenticationTokenFilter;
import com.neu.monitorSys.auth.handler.AccessDeniedHandlerImpl;
import com.neu.monitorSys.auth.handler.MyLogoutHandler;
import com.neu.monitorSys.auth.handler.MyLogoutSuccessHandler;
import com.neu.monitorSys.auth.manager.MyAccessManager;
import com.neu.monitorSys.auth.service.impl.MyUserDetailsService;
import com.neu.monitorSys.auth.utils.RedisUtil;
import com.neu.monitorSys.common.DTO.MyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class AuthConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    //5分钟
    public static long tokenExpireTime = 5 * 60 * 1000;
    @Autowired
    private SmsCodeSecurityConfig smsCodeSecurityConfig;
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired
    private CustomDsl customDsl;

    @Autowired
    private RoleClient roleClient;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;
    @Autowired
    private MyLogoutHandler myLogoutHandler;




    public static final String[] whiteList = new String[]{"/api/v1/auth/public/register", "/api/v1/auth/login", "/api/v1/code/sms", "/api/v1/auth/login/mobile","/api/v1/auth/normal/register", "/error"};

    /**
     * 身份认证管理器，调用authenticate()方法完成认证
     */
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        Set<String> permitAll = Set.of("/api/v1/auth/public/register", "/api/v1/auth/login", "/api/v1/code/sms", "/api/v1/auth/login/mobile","/api/v1/auth/normal/register", "/error");
        http.csrf(AbstractHttpConfigurer::disable)
                //不通过Session获取SecurityContext
                .apply(customDsl)
                .and()
                .sessionManagement(sessionManager -> {
                    sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .httpBasic(httpSecurityHttpBasicConfigurer -> {
                    httpSecurityHttpBasicConfigurer.disable();
                })
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/v1/auth/public/register", "/api/v1/auth/login", "/api/v1/code/sms", "/api/v1/auth/login/mobile", "/error").permitAll()
                        .anyRequest().access(new MyAccessManager(permitAll, roleClient, redisUtil))
                )
//                .requestMatchers("/auth/register", "/auth/validate", "/auth/login", "/code/sms", "/login/mobile").permitAll()
                .apply(smsCodeSecurityConfig)
                .and()
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling
                            .accessDeniedHandler((request, response, accessDeniedException) -> {
                                MyResponse<String> myResponse = new MyResponse<>(403, accessDeniedException.getMessage(), null);
                                response.getWriter().write(JSONUtil.toJsonStr(myResponse));
                            })
                            //登录失败返回401状态码
                            //.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                            .accessDeniedHandler(accessDeniedHandler)
                    ;
                    System.out.println("认证鉴权错误");
                });

        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.logout(
                logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(myLogoutHandler)
                        .logoutSuccessHandler(new MyLogoutSuccessHandler())
        );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        LoginAuthenticationProvider loginAuthenticationProvider = new LoginAuthenticationProvider();
//        BeanUtil.copyProperties(authenticationProvider, loginAuthenticationProvider);

        return authenticationProvider;
    }

    @Bean
    public MyUserDetailsService userDetailsService() {
        return new MyUserDetailsService();
    }

}