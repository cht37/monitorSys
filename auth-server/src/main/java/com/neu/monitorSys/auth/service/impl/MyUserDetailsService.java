package com.neu.monitorSys.auth.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.neu.monitorSys.auth.client.UserClient;
import com.neu.monitorSys.auth.entity.CustomUserDetails;
import com.neu.monitorSys.auth.entity.Member;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService {

    @Resource
    private UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String logId) throws UsernameNotFoundException {
        Member user = null;

        Object login = userClient.getMember(logId, "login").getData();
        if (login == null) {
            login = userClient.getMemberByMobile(logId).getData();
        }
        user = BeanUtil.toBean(login, Member.class);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new CustomUserDetails(user);
    }
}