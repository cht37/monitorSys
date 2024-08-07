package com.neu.monitor_sys.statistics.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.neu.monitor_sys.common.DTO.AqiDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatisticsVO {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 对应反馈信息编号
     */
    private Integer afId;
    /**
     * 所属省区域编号
     */
    private String provinceId;
    /**
     * 所属省区域名称
     */
    private String provinceName;
    /**
     * 所属市区域编号
     */
    private String cityId;
    /**
     * 所属市区域名称
     */
    private String cityName;
    /**
     * 所属区区域编号
     */
    private String districtId;
    /**
     * 所属区区域名称
     */
    private String districtName;
    /**
     * 反馈信息所在区域详细地址
     */
    private String address;

    /**
     * 实测空气二氧化硫浓度（μg/m3）
     */
    private Integer so2Value;
    /**
     * S02级别
     */
    private AqiDTO s02Level;
    /**
     * so2_aqi
     */
    private Integer so2Aqi;

    /**
     * 实测空气一氧化碳浓度（mg/m3）
     */
    private Integer coValue;
    /**
     * CO级别
     */
    private AqiDTO coLevel;
    /**
     * co_aqi
     */
    private Integer coAqi;

    /**
     * 实测空气悬浮颗粒物浓度（μg/m3）
     */
    private Integer spmValue;

    /**
     * SPM级别
     */
    private AqiDTO spmLevel;
    /**
     * spm_aqi
     */
    private Integer spmAqi;

    /**
     * 最大空气质量指数
     */
    private Integer aqi;
    /**
     * 空气质量级别
     */
    private AqiDTO aqiLevel;

    /**
     * 确认日期
     */
    private LocalDateTime confirmDatetime;

    /**
     * 所属网格员编号，对应grid_manager表
     */
    private String gmId;

    /**
     * 网格员姓名
     */
    private String gmName;

    /**
     * 反馈者手机号（公众监督员）
     */
    private String fdTel;

    /**
     * 反馈者信息描述
     */
    private String information;

    /**
     * 备注
     */
    private String remarks;

}
