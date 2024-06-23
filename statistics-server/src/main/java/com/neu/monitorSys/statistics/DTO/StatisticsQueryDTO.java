package com.neu.monitorSys.statistics.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatisticsQueryDTO {
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 对应反馈信息编号
     */
    private Integer afId;

    /**
     * 所属省区域名称
     */
    private String provinceName;


    /**
     * 所属市区域名称
     */
    private String cityName;


    /**
     * 所属区区域名称
     */
    private String districtName;
    /**
     * 反馈信息所在区域详细地址
     */
    private String address;

    /**
     * 空气二氧化硫浓度（μg/m3）上限
     */
    private Integer so2ValueMax;
    /**
     * 空气二氧化硫浓度（μg/m3）下限
     */
    private Integer so2ValueMin;

    /**
     * 按SO2排序
     */
    private Boolean so2Ascending;

    /**
     * 一氧化碳浓度（mg/m3）上限
     */
    private Integer coValueMax;
    /**
     * 一氧化碳浓度（mg/m3）下限
     */
    private Integer coValueMin;
    /**
     * 按CO排序
     */
    private Boolean coAscending;

    /**
     * 空气悬浮颗粒物浓度（μg/m3）上限
     */
    private Integer spmValueMax;
    /**
     * 空气悬浮颗粒物浓度（μg/m3）下限
     */
    private Integer spmValueMin;

    /**
     * 按SPM排序
     */
    private Boolean spmAscending;

    /**
     * 实测空气质量指数上限
     */
    private Integer aqiMax;
    /**
     * 实测空气质量指数下限
     */
    private Integer aqiMin;

    /**
     * 按AQI排序
     */
    private Boolean aqiAscending;
    /**
     * 确认日期
     */
    private LocalDateTime confirmDatetime;

    /**
     * 所属网格员编号，对应grid_manager表
     */
    private Integer gmId;


    /**
     * 反馈者手机号（公众监督员）
     */
    private String fdTel;




}
