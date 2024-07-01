package com.neu.monitorSys.auth.DTO;

import com.neu.monitorSys.common.DTO.MemberWithRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {
   private MemberWithRole memberWithRole;
   private String jwt;
}
