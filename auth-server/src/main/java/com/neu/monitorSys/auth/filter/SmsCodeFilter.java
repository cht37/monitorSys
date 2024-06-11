package com.neu.monitorSys.auth.filter;

import cn.hutool.core.util.StrUtil;
import com.neu.monitorSys.auth.entity.SmsCode;
import com.neu.monitorSys.auth.exception.ValidateCodeException;
import com.neu.monitorSys.auth.utils.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;

@Component
public class SmsCodeFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (StrUtil.equalsIgnoreCase("/login/mobile", httpServletRequest.getRequestURI())
                && StrUtil.equalsIgnoreCase(httpServletRequest.getMethod(), "post")) {
            try {
                validateCode(new ServletWebRequest(httpServletRequest));
            } catch (ValidateCodeException e) {
                authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, new AuthenticationException(e.getMessage()) {
                });
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void validateCode(ServletWebRequest servletWebRequest) throws ServletRequestBindingException, ValidateCodeException {
        String smsCode = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "smsCode");
        String mobile = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "mobile");
        String code=(String) redisUtil.get("sms:" + mobile);
        int expire= (int) redisUtil.getExpire("sms:" + mobile);
        SmsCode codeInRedis = new SmsCode(code, expire);

        if (StrUtil.isBlank(smsCode)) {
            throw new ValidateCodeException("验证码不能为空");
        }

        if (codeInRedis == null) {
            throw new ValidateCodeException("验证码不存在");
        }

        if (codeInRedis.isExpire()) {
            throw new ValidateCodeException("验证码已经过期");
        }

        if (!StrUtil.equalsIgnoreCase(codeInRedis.getCode(), smsCode)) {
            throw new ValidateCodeException("验证码不正确");
        }

        //从redis中删除验证码
        redisUtil.del("sms:" + mobile);
    }
}
