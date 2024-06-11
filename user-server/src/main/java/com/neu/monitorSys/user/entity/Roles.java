package com.neu.monitorSys.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-07
 */
@Getter
@Setter
  public class Roles implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 角色名称
     */
      private String mname;

      /**
     * 记录状态码：1正常
     */
      private Integer enable;

    private String remark;


}
