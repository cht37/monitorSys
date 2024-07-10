package com.neu.monitor_sys.feedback.DTO;

import lombok.Data;

@Data
public class AqiFeedbackDTO {

    /**
     * 公众监督员电话号码
     */
    private String telId;

    /**
     * 反馈信息所在省区域名称
     */
    private String provinceName;

    /**
     * 反馈信息所在市区域名称
     */
    private String cityName;

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
    private String afDate;

    /**
     * 反馈时间
     */
    private String afTime;

    /**
     * 反馈状态，0未指派，1已指派，2正在处理，3已处理
     */
    private Integer state;

    /**
     * 备注
     */
    private String remarks;
}
