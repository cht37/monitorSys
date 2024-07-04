package com.neu.monitorSys.statistics.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitorSys.common.DTO.MyResponse;
import com.neu.monitorSys.common.constants.ResultCode;
import com.neu.monitorSys.statistics.DTO.PollutionStatisticsDTO;
import com.neu.monitorSys.statistics.DTO.ProvinceAqiStatsDTO;
import com.neu.monitorSys.statistics.DTO.ReportDTO;
import com.neu.monitorSys.statistics.DTO.StatisticsQueryDTO;
import com.neu.monitorSys.statistics.VO.StatisticsVO;
import com.neu.monitorSys.statistics.entity.AqiStatisticsPercent;
import com.neu.monitorSys.common.entity.StatisticsES;
import com.neu.monitorSys.statistics.service.IStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
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

    /**
     * 网格员上报统计信息
     *
     * @param reportDTO 上报信息
     * @param logId     用户id（网格员）
     * @return 是否成功
     */
    @PostMapping
    public MyResponse<Boolean> submitStatistics(@RequestBody ReportDTO reportDTO, @RequestHeader("logId") String logId) {
        try {
            statisticsService.gridManagerReport(reportDTO, logId);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), e.getMessage(), false);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", true);
    }

    /**
     * 分条件查询统计数据
     *
     * @param statisticsQueryDTO 查询条件
     * @param page               页数
     * @param size               每页大小
     * @return 统计数据（分页）
     */
    @GetMapping("/search")
    public MyResponse<IPage<StatisticsVO>> searchStatistics(
            @ModelAttribute StatisticsQueryDTO statisticsQueryDTO,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<StatisticsVO> statisticsVO = null;
        try {
            statisticsVO = statisticsService.queryStatisticsData(statisticsQueryDTO, page, size);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", statisticsVO);
    }

    /**
     * 根据id查询统计数据
     *
     * @param logId 用户id
     * @param page  页数
     * @param size  每页大小
     * @return 统计数据（分页）
     */
    @GetMapping("/history")
    public MyResponse<IPage<StatisticsVO>> searchStatisticsById(
            @RequestHeader("logId") String logId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<StatisticsVO> statisticsVO = null;
        try {
            statisticsVO = statisticsService.queryStatisticsDataById(logId, page, size);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", statisticsVO);
    }


    /**
     * 多条件查询统计数据ES
     *
     * @param statisticsQueryDTO 查询条件
     * @param page               页数
     * @param size               每页大小
     * @return 统计数据（分页）
     */
    @GetMapping("/es/search")
    public MyResponse<IPage<StatisticsVO>> searchStatisticsES(
            @ModelAttribute StatisticsQueryDTO statisticsQueryDTO,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<StatisticsVO> searchPage= null;
        try {
            searchPage = statisticsService.queryStatisticsDataES(statisticsQueryDTO, page, size);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", searchPage);
    }


     @GetMapping("/search/all")
    public SearchPage<StatisticsES> searchAllStatistics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return statisticsService.queryAllStatisticsData(page, size);
    }

    @GetMapping("/search/all/list")
    public MyResponse<List<SearchHit<StatisticsES>>> searchAllStatisticsList() {
        List<SearchHit<StatisticsES>> searchHits = null;
        try {
            searchHits = statisticsService.queryAllStatisticsData();
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", searchHits);
    }

    /**
     * 获取省级空气质量统计信息
     * @return 省级空气质量统计信息
     */
    @GetMapping("/province")
    public MyResponse<List<ProvinceAqiStatsDTO>> getProvinceAqiStatistics() {
        List<ProvinceAqiStatsDTO> provinceAqiStatsDTO = null;
        try {
            provinceAqiStatsDTO = statisticsService.getProvinceAqiStatistics();
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", provinceAqiStatsDTO);
    }

    /**
     * 获取各级别空气质量所占比例
     * @return 各级别空气质量所占比例
     */
    @GetMapping("/aqi/percent")
    public MyResponse<List<AqiStatisticsPercent>> getAqiLevelPercent() {
        List<AqiStatisticsPercent> aqiStatisticsPercent = null;
        try {
            aqiStatisticsPercent = statisticsService.getAqiLevelPercent();
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", aqiStatisticsPercent);
    }


    /**
     * 根据省份聚合，获取三种污染物超标次数
     * 超标：指的是污染等级大于等于4级
     * @param level 污染等级
     * @return 污染物超标次数
     */
    @GetMapping("/pollution")
    public MyResponse<List<PollutionStatisticsDTO>> getProvincePollutionStats(@RequestParam Integer level) {
        List<PollutionStatisticsDTO> pollutionStatisticsDTO = null;
        try {
            pollutionStatisticsDTO = statisticsService.getProvincePollutionStats(level);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(), e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", pollutionStatisticsDTO);
    }
}

