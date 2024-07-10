package com.neu.monitor_sys.auth.DTO;

import lombok.Data;

@Data
public class AuthRequest {
    private String logId;
    private String logPwd;
}
