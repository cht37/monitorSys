package com.neu.monitor_sys.statistics.DTO;

import lombok.Data;

@Data
public class ReportDTO {
    /**
     * feedback id
     */
    private Integer afId;

    /**
     * 实测空气二氧化硫浓度（μg/m3）
     */
    private Integer so2Value;

    /**
     * 实测空气一氧化碳浓度（mg/m3）
     */
    private Integer coValue;

    /**
     * 实测空气悬浮颗粒物浓度（μg/m3）
     */
    private Integer spmValue;
    /**
     * remarks
     *
     */
    private String remarks;
}
