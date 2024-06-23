package com.neu.monitorSys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("cities")
  public class Cities implements Serializable {

    private static final long serialVersionUID = 1L;
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
      /**
     * 城市id
     */
      private String cityId;

      /**
     * 城市名称
     */
      private String cityName;

      /**
     * 所属省份id
     */
      private String provinceId;


}
