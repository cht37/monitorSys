package com.neu.monitorSys.geography.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
  @TableName("grid_manager_area")
public class GridManagerArea implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

    private Integer cityId;

    private String areaName;


}
