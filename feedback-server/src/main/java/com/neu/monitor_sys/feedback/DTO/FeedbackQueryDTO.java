package com.neu.monitor_sys.feedback.DTO;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
public class FeedbackQueryDTO {

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
     * 反馈者对空气质量指数级别的预估等级
     */
    private Integer estimatedGrade;

    /**
     * 反馈日期起
     */
    private String afDateStart;

    /**
     * 反馈日期排序
     */
    private Boolean afDateAscending;

    /**
     * 反馈日期止
     */
    private String afDateEnd;

      /**
     * 网格员id(账号)
     */
    private String gridManager_id;

    /**
     * 指派日期起
     */
    private String assignDateStart;

    /**
     * 指派日期止
     */
    private String assignDateEnd;

    /**
     * 指派日期排序
     */
    private Boolean assignDateAscending;

    /**
     * 指派状态
     */
    private Integer assignStatus;

}
