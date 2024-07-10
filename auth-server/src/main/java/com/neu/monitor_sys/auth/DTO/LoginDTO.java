package com.neu.monitor_sys.auth.DTO;

import com.neu.monitor_sys.common.DTO.MemberWithRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {
   private MemberWithRole memberWithRole;
   private String jwt;
}
