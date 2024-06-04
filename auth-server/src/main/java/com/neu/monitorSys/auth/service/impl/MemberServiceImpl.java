package com.neu.monitorSys.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.auth.entity.Member;
import com.neu.monitorSys.auth.mapper.MemberMapper;
import com.neu.monitorSys.auth.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper,Member> implements MemberService {
    @Resource
    MemberMapper memberMapper;
    @Override
    public Member getMember(String logId) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getLogid, logId);
        return memberMapper.selectOne(wrapper);
    }
    @Override
    public Member saveMember(Member member) {
        return null;
    }
}