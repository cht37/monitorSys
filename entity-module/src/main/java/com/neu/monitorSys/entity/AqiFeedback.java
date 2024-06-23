package com.neu.monitorSys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

/**
 * <p>
 *
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-11
 */
@Getter
@Setter
@TableName("aqi_feedback")
public class AqiFeedback implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 反馈信息编号
     */
    @TableId(value = "af_id", type = IdType.AUTO)
    private Integer afId;

    /**
     * 公众监督员电话号码
     */
    private String telId;

    /**
     * 反馈信息所在省区域编号
     */
    private String provinceId;

    /**
     * 反馈信息所在市区域编号
     */
    private String cityId;
    /**
     * 反馈信息所在区区域编号
     */
    private String districtId;
    /**
     * 反馈信息所在区域详细地址
     */
    private String address;

    /**
     * 反馈信息描述
     */
    private String information;

    /**
     * 反馈者对空气质量指数级别的预估等级
     */
    private Integer estimatedGrade;

    /**
     * 反馈日期
     */
    private Date afDate;

    /**
     * 反馈时间
     */
    private Time afTime;

    /**
     * 指派网格员编号(grid_manager表对应主键)
     */
    private String gmId;

    /**
     * 指派日期
     */
    private Date assignDate;

    /**
     * 指派时间
     */
    private Time assignTime;

    /**
     * 信息状态 (0:未指派, 1:已指派, 2:已确认)
     */
    private Integer state;

    /**
     * 备注
     */
    private String remarks;


    public double getTimestamp() {
        return afDate.getTime() + afTime.getTime();
    }
}
