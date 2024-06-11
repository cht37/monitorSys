package com.neu.monitorSys.geography.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

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

      /**
     * 省份id
     */
        @TableId(value = "province_id", type = IdType.AUTO)
      private Integer provinceId;

      /**
     * 省份名称
     */
      private String provinceName;


}
