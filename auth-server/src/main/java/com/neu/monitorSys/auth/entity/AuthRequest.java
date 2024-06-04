package com.neu.monitorSys.auth.entity;

import lombok.Data;

@Data
public class AuthRequest {
    private String logId;
    private String logPwd;
}
