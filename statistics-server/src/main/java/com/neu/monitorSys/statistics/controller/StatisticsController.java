package com.neu.monitorSys.statistics.controller;


import com.neu.monitorSys.entity.DTO.MyResponse;
import com.neu.monitorSys.entity.constants.ResultCode;
import com.neu.monitorSys.statistics.DTO.ReportDTO;
import com.neu.monitorSys.statistics.service.IStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-17
 */
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {
    @Autowired
    private IStatisticsService statisticsService;
    @PostMapping
    public MyResponse<Boolean> submitStatistics(@RequestBody ReportDTO reportDTO, @RequestHeader("logId") String logId){
        try {
            statisticsService.gridManagerReport(reportDTO,logId);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), e.getMessage(),false);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success",true);
    }

}

