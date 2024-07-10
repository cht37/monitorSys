package com.neu.monitor_sys.statistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitor_sys.common.entity.Statistics;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neu.monitor_sys.statistics.DTO.PollutionStatisticsDTO;
import com.neu.monitor_sys.statistics.DTO.ProvinceAqiStatsDTO;
import com.neu.monitor_sys.statistics.DTO.ReportDTO;
import com.neu.monitor_sys.statistics.DTO.StatisticsQueryDTO;
import com.neu.monitor_sys.statistics.VO.StatisticsVO;
import com.neu.monitor_sys.statistics.entity.AqiStatisticsPercent;
import com.neu.monitor_sys.common.entity.StatisticsES;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;

import java.io.IOException;
import java.util.List;

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

        //根据id查询统计数据
        IPage<StatisticsVO> queryStatisticsDataById(String logId , int page, int size);


        //多条件查询统计数据ES
         IPage<StatisticsVO> queryStatisticsDataES(StatisticsQueryDTO statisticsQueryDTO, int page, int size);

        SearchPage<StatisticsES> queryAllStatisticsData(int page, int size);


        List<SearchHit<StatisticsES>> queryAllStatisticsData();

        /**
         * 获取最近一周省级空气质量统计信息
         * @return 省级空气质量统计信息
         */
        List<ProvinceAqiStatsDTO> getProvinceAqiStatistics() throws IOException;

        /**
         * 获取各级别空气质量所占比例
         */
        List<AqiStatisticsPercent> getAqiLevelPercent() throws IOException;


        /**
         * 根据省份聚合，获取三种污染物超标次数
         * 超标：指的是污染等级大于等于4级
         */
        List<PollutionStatisticsDTO> getProvincePollutionStats(Integer level) throws IOException;
}
