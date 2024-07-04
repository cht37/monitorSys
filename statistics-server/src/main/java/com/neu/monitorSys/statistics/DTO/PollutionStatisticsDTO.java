package com.neu.monitorSys.statistics.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PollutionStatisticsDTO {
    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * so2超标次数
     */
    private Integer so2ExceedTimes;

    /**
     * co超标次数
     */
    private Integer coExceedTimes;

    /**
     * spm超标次数
     */
    private Integer spmExceedTimes;

    /**
     * 时间
     */
    private LocalDateTime time;




}
