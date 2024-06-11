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
  public class Cities implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 城市id
     */
        @TableId(value = "city_id", type = IdType.AUTO)
      private Integer cityId;

      /**
     * 城市名称
     */
      private String cityName;

      /**
     * 所属省份id
     */
      private Integer provinceId;


}
