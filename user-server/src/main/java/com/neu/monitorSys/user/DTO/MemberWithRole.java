package com.neu.monitorSys.user.DTO;

import com.neu.monitorSys.user.entity.Member;
import com.neu.monitorSys.user.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberWithRole {
    private Member member;
    private Roles roles;
}
