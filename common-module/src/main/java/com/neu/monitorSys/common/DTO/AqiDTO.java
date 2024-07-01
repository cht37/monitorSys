package com.neu.monitorSys.common.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AqiDTO {
    /**
     * Aqi id
     */
    private int id;
    /**
     * Aqi 级别
     */
    private String chineseExplain;
    /**
     * Aqi 对应的颜色
     */
    private String color;
}
