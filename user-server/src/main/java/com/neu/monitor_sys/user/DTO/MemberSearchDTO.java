package com.neu.monitor_sys.user.DTO;

import lombok.Data;


@Data
public class MemberSearchDTO {
    /**
     * 用户id
     */
    private String logId;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 用户手机号
     */
    private String mobile;
    /**
     * 用户性别
     */
    private String gender;
    /**
     * 用户状态
     */
    private Integer state;
    /**
     * 用户角色字符串，不同角色用逗号隔开
     */
    private String roles;
}
