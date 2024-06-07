package com.neu.monitorSys.user.DTO;

import com.neu.monitorSys.user.entity.Member;
import lombok.Data;

@Data
public class MemberWithRole {
    private Member member;
    private String roleName;
}
