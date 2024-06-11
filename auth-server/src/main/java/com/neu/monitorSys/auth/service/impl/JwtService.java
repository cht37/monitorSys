package com.neu.monitorSys.auth.service.impl;

import com.neu.monitorSys.auth.utils.JwtUtil;
import com.neu.monitorSys.auth.constants.AuthRedisPrefix;
import com.neu.monitorSys.auth.utils.RedisUtil;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

import static com.neu.monitorSys.auth.utils.JwtUtil.decode;

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
    public boolean validateToken(String token) throws ParseException, JOSEException {
        //验证token
        if (!decode(token)) {
            return false;
        }
        //验证token是否在redis中
        return redisUtil.hasKey("Tokens:" + token);


    }
}
