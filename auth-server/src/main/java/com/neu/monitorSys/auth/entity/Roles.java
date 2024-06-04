package com.neu.monitorSys.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-01
 */
@Data
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
