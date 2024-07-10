package com.neu.monitor_sys.auth.DTO;

import lombok.Data;

@Data
public class ModifyPasswordDTO {
    /**
     * 原密码（新用户可为空）
     */
    private String originPassword;
    /**
     * 新密码
     */
    private String newPassword;
}
