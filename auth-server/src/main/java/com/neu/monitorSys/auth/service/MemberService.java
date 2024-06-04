package com.neu.monitorSys.auth.service;

import com.neu.monitorSys.auth.entity.Member;
import org.springframework.stereotype.Service;


public interface AuthService {
    //保存用户
    Member save(Member member);
    
}
