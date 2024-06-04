package com.neu.monitorSys.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表	
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
@Getter
@Setter
  public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 人员真实姓名，
     */
      private String mnane;

      /**
     * 登录名
     */
      private String logid;

      /**
     * 登录密码
     */
      private String logpwd;

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
      private LocalDate birthday;

      /**
     * 角色编码
     */
      private Integer roleid;

      /**
     * 人员状态，	默认为0: 未指派	1：已指派身份
     */
      private Integer state;

//  public Member(String mnane, String logid, String tel, String gender, LocalDate birthday) {
//    id= null;
//    logpwd=null;
//    roleid=null;
//    state=null;
//    this.mnane = mnane;
//    this.logid = logid;
//    this.tel = tel;
//    this.gender = gender;
//    this.birthday = birthday;
//  }
}
