package com.neu.monitorSys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("districts")
public class District implements Serializable {
     private static final long serialVersionUID = 1L;
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
      /**
     * 区id
     */
      private String districtId;

      /**
     * 区名称
     */
      private String districtName;

      /**
     * 所属城市id
     */
      private String cityId;
}
