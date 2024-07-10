//package com.neu.monitorSys.auth.provider;
//
//import com.neu.monitorSys.auth.service.impl.MyUserDetailsService;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.InternalAuthenticationServiceException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
//
//public class LoginAuthenticationProvider extends DaoAuthenticationProvider {
//    private MyUserDetailsService userDetailsService;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//
//        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;
//        UserDetails userDetails = userDetailsService.loadUserByUsername((String) authentication.getPrincipal());
//        if (userDetails == null) {
//            throw new InternalAuthenticationServiceException("未找到登录用户");
//        }
//        UsernamePasswordAuthenticationToken authenticationResult = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
//        authenticationResult.setDetails(usernamePasswordAuthenticationToken.getDetails());
//        return authenticationResult;
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return false;
//    }
//}
