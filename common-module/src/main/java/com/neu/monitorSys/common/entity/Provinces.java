package com.neu.monitorSys.common.entity;

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
 * @since 2024-06-10
 */
@Getter
@Setter
  public class Provinces implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
      /**
     * 省份id
     */
      private String provinceId;

      /**
     * 省份名称
     */
      private String provinceName;


}
