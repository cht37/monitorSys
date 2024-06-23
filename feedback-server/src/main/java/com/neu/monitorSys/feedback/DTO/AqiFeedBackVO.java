package com.neu.monitorSys.feedback.DTO;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
public class AqiFeedBackVO {
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
     * 反馈信息所在省区域名称
     */
    private String provinceName;

    /**
     * 反馈信息所在市区域编号
     */
    private String cityId;

    /**
     * 反馈信息所在市区域名称
     */
    private String cityName;

    /**
     * 反馈信息所在区区域编号
     */
    private String districtId;

    /**
     * 反馈信息所在区区域名称
     */
    private String districtName;

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
     * 指派网格员编号(grid_manager表对应member_id)
     */
    private String gmId;
    /**
     * 指派网格员姓名
     */
    private String gmName;

    /**
     * 指派日期
     */
    private Date assignDate;

    /**
     * 指派时间
     */
    private Time assignTime;

    /**
     * 确认时间
     */
    private Date confirmDatetime;

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
