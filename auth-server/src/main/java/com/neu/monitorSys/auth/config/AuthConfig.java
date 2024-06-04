//package com.neu.monitorSys.auth.config;
//
//import com.neu.monitorSys.auth.handler.LoginFailHandler;
//import com.neu.monitorSys.auth.handler.LoginSuccessHandler;
//import com.neu.monitorSys.auth.handler.NoLoginHandler;
//import com.neu.monitorSys.auth.service.impl.MyUserDetailsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true) //作用：自动开启注解式授权
//public class SecurityConfig extends WebSecurityConfiguration {
//    @Autowired
//    private LoginSuccessHandler loginSuccessHandler;
////    @Autowired
////    private MyUserDetailsService myUserDetailsService;
////
////    @Override
////    public void configure(AuthenticationManagerBuilder auth) throws Exception {
////        auth.userDetailsService(myUserDetailsService);
////    }
//
//    /**
//     * 自定义登录页面
//     *
//     * @param http
//     * @throws Exception
//     */
//    public void configure(HttpSecurity http) throws Exception {
//        http.formLogin() //告诉框架自定义页面
//                .loginProcessingUrl("/user/login") //对应表单提交的action
//                .successHandler(loginSuccessHandler)
//                .failureHandler(new LoginFailHandler())
//                .permitAll();//对login.html和dologin请求放行
//        http.exceptionHandling()
//                .authenticationEntryPoint(new NoLoginHandler()); //未登录处理
//        http.authorizeRequests()
//                .requestMatchers("/auth/register").permitAll()
//                .requestMatchers("/auth/login").permitAll()
//                .anyRequest().authenticated(); //所有请求都拦截
//        /**
//         * 把jwtfilter注入进来
//         */
////        http.addFilterAfter(jWTFilter, UsernamePasswordAuthenticationFilter.class);
//        /**
//         * 把session禁掉
//         */
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//        //防跨站脚本攻击关闭
//        http.csrf().disable();
//        //运行跨域
//        http.cors();
//    }
//
//    /**
//     * 数据加密类
//     *
//     * @return
//     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}