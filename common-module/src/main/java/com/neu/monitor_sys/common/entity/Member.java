package com.neu.monitor_sys.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-01
 */
@Data
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 人员真实姓名，
     */
    private String mname;

    /**
     * 登录名
     */
    private String logid;

//      /**
//     * 登录密码
//     */
//      private String logpwd;

    /**
     * 手机号
     */
    private String tel;

    /**
     * 性别
     */
    private String gender;

    /**
     * 生日
     */
    private Date birthday;

//      /**
//     * 角色编码
//     */
//      private Integer roleid;

    /**
     * 人员状态，默认为1：正常
     */
    private Integer state;

    /**
     * 是否为新用户，默认为1：是
     */
    private Integer isNew;
}
