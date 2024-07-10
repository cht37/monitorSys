package com.neu.monitor_sys.auth.service.impl;

import com.neu.monitor_sys.auth.utils.JwtUtil;
import com.neu.monitor_sys.common.constants.AuthRedisPrefix;
import com.neu.monitor_sys.auth.utils.RedisUtil;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.text.ParseException;

@Service
public class JwtService {
    @Autowired
    private JwtUtil jwtUtil;
     @Autowired
    private RedisUtil redisUtil;
    public String createToken(String username) throws JOSEException {
        String token= null;
        if (!redisUtil.hasKey(AuthRedisPrefix.AUTH_PREFIX +username)) {
            token = JwtUtil.createToken(username);
            //加入redis
            redisUtil.set(AuthRedisPrefix.AUTH_PREFIX +username,token, 60 * 60 * 2);
        }else {
            token = (String) redisUtil.get(AuthRedisPrefix.AUTH_PREFIX +username);
        }
        return token;
    }
    public String validateToken(String token) throws ParseException, JOSEException, AuthenticationException {
        //获取线程id
        long id = Thread.currentThread().getId();
        //删除redis认证信息
        redisUtil.del(AuthRedisPrefix.USER_AUTHENTICATION_PREFIX+id);
        //解析jwt
        String userName = JwtUtil.getPayLoad(token).get("logId").toString();
         //验证token是否在redis中
        if (!redisUtil.hasKey(AuthRedisPrefix.AUTH_PREFIX+userName)){
            throw new AuthenticationException("登录过期，请重新登录");
        }

        //获得redis中token有效期
        long expire = redisUtil.getExpire(AuthRedisPrefix.AUTH_PREFIX + userName);
        //如果有效期小于等于半小时，续期
        if (expire <= 60 * 30) {
            redisUtil.expire(AuthRedisPrefix.AUTH_PREFIX + userName, 60 * 60 * 2);
            redisUtil.expire(AuthRedisPrefix.USER_CUSTOM_DETAIL_PREFIX+userName,60 * 60 * 2);
            redisUtil.expire(AuthRedisPrefix.USER_ROLE_PREFIX+userName,60 * 60 * 2);
        }

        return userName;
    }
}
