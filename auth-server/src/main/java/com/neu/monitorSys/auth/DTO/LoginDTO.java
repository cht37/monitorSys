package com.neu.monitorSys.auth.DTO;

import com.neu.monitorSys.entity.DTO.MemberWithRole;
import com.neu.monitorSys.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {
   private MemberWithRole memberWithRole;
   private String jwt;
}
