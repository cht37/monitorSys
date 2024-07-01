package com.neu.monitorSys.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-17
 */
@Getter
@Setter
public class Statistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计信息编号
     */
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
     * 所属市区域编号
     */
    private String cityId;

    /**
     * 所属区区域编号
     */
    private String districtId;

    /**
     * 反馈信息所在区域详细地址
     */
    private String address;

    /**
     * 实测空气二氧化硫浓度（μg/m3）
     */
    private Integer so2Value;

    /**
     * so2_aqi
     */
    private Integer so2Aqi;

    /**
     * 实测空气一氧化碳浓度（mg/m3）
     */
    private Integer coValue;

    /**
     * co_aqi
     */
    private Integer coAqi;

    /**
     * 实测空气悬浮颗粒物浓度（μg/m3）
     */
    private Integer spmValue;

    /**
     * spm_aqi
     */
    private Integer spmAqi;


    /**
     * 实测空气质量指数
     */
    private Integer aqi;

    /**
     * 确认日期
     */
    private LocalDateTime confirmDatetime;

    /**
     * 所属网格员编号，对应grid_manager表
     */
    private String gmId;

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
