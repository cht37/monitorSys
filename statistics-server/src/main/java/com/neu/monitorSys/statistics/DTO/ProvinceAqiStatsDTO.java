package com.neu.monitorSys.statistics.DTO;

import lombok.Data;

@Data
public class ProvinceAqiStatsDTO {
    private String provinceId;
    private double avgSo2Value;
    private double avgCoValue;
    private double avgSpmValue;
    private double avgAqi;

    // Getters and setters
}
