package com.neu.monitorSys.auth.service;

import com.neu.monitorSys.auth.entity.Member;


public interface MemberService {
    //保存用户
    Member saveMember(Member member);
    //根据用户账号查询用户
    Member getMember(String logId);
}
