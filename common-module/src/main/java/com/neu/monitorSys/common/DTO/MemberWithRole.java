package com.neu.monitorSys.common.DTO;

import com.neu.monitorSys.common.entity.Member;
import com.neu.monitorSys.common.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberWithRole {
    private Member member;
    //角色信息
    private List<Roles> roles;
    //权限列表
    Set<String> permissions;
}
