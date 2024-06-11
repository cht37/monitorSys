package com.neu.monitorSys.auth.provider;

import com.neu.monitorSys.auth.entity.CustomUserDetails;
import com.neu.monitorSys.auth.filter.SmsAuthenticationToken;
import com.neu.monitorSys.auth.service.impl.MyUserDetailsService;
import lombok.Data;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Data
@Component
public class SmsAuthenticationProvider implements AuthenticationProvider {

    private MyUserDetailsService userDetailsService;

    /**
     *authenticate方法用于编写具体的身份认证逻辑。
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsAuthenticationToken smsAuthenticationToken = (SmsAuthenticationToken) authentication;
        UserDetails userDetails = userDetailsService.loadUserByUsername((String) authentication.getPrincipal());

        if (userDetails == null) {
            throw new InternalAuthenticationServiceException("未找到与该手机对应的用户");
        }

        SmsAuthenticationToken authenticationResult = new SmsAuthenticationToken(userDetails.getAuthorities(), userDetails);
        authenticationResult.setDetails(smsAuthenticationToken.getDetails());
        return authenticationResult;
    }

    /**
     * supports方法指定了支持处理的Token类型为SmsAuthenticationToken
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return SmsAuthenticationToken.class.isAssignableFrom(aClass);
    }

}
