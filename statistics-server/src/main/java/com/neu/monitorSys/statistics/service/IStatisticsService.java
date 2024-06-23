package com.neu.monitorSys.statistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitorSys.entity.Statistics;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neu.monitorSys.statistics.DTO.ReportDTO;
import com.neu.monitorSys.statistics.DTO.StatisticsQueryDTO;
import com.neu.monitorSys.statistics.VO.StatisticsVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-17
 */
public interface IStatisticsService extends IService<Statistics> {
        //统计分析数据
        void statisticsData(ReportDTO reportDTO);
        //网格员上报数据
        void gridManagerReport(ReportDTO reportDTO,String logId);
        //分条件查询统计数据
        IPage<StatisticsVO> queryStatisticsData(StatisticsQueryDTO statisticsQueryDTO, int page, int size);

}
