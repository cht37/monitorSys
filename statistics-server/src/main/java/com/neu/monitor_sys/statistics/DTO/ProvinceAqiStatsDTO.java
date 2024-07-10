package com.neu.monitor_sys.statistics.DTO;

import lombok.Data;


@Data
public class ProvinceAqiStatsDTO {
    private String provinceId;
    private String provinceName;
    private double avgSo2Value;
    private double avgCoValue;
    private double avgSpmValue;
    private double avgAqi;



    // Getters and setters
}
