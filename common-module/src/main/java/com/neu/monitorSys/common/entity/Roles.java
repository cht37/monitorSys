package com.neu.monitorSys.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

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
     * 别名（中文名）
     */
    private String nickName;


    /**
     * 记录状态码：1正常
     */
    private Integer enable;
    /**
     * 备注
     */
    private String remark;


}
