package com.neu.monitorSys.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 网格员表
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
@Getter
@Setter
  @TableName("grid_manager")
public class GridManager implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 网格员id
     */
        @TableId(value = "gm_id", type = IdType.AUTO)
      private Integer gmId;

      /**
     * 成员id
     */
      private Integer memberId;

      /**
     * 所负责区域id
     */
      private Integer areaId;

      /**
     * 状态	0：可工作状态	1：临时抽调	2：休假	3：其他
     */
      private Integer state;

      /**
     * 网格员个人信息
     */
      private String remark;


}
