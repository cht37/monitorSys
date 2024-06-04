package com.neu.monitorSys.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.auth.entity.CustomUserDetails;
import com.neu.monitorSys.auth.entity.Member;
import com.neu.monitorSys.auth.mapper.MemberMapper;
import com.neu.monitorSys.auth.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class MyUserDetailsService extends ServiceImpl<MemberMapper,Member> implements UserDetailsService {

    @Autowired
    private MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String logId) throws UsernameNotFoundException {
        Member user = memberService.getMember(logId);
        Optional<Member> credential = Optional.ofNullable(user);
        //TODO 查询失败处理
        return credential.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }
}