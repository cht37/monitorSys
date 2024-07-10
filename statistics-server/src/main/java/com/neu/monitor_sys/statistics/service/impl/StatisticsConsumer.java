package com.neu.monitor_sys.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.neu.monitor_sys.common.config.RabbitMQCommonConfig;
import com.neu.monitor_sys.common.entity.Statistics;
import com.neu.monitor_sys.statistics.DTO.ReportDTO;
import com.neu.monitor_sys.statistics.client.AqiClient;
import com.neu.monitor_sys.statistics.mapper.StatisticsMapper;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsConsumer {
    @Autowired
    private StatisticsMapper statisticsMapper;
    @Autowired
    private AqiClient aqiClient;
    @RabbitListener(queuesToDeclare = @Queue(RabbitMQCommonConfig.DATA_QUEUE))
    public void saveAqiData(ReportDTO reportDTO){
        int[] data = aqiClient.calculateAqi(reportDTO.getSo2Value(), reportDTO.getCoValue(), reportDTO.getSpmValue()).getData();
        if(data!=null){
            UpdateWrapper<Statistics> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("af_id",reportDTO.getAfId());
            Statistics statistics = new Statistics();
            statistics.setSo2Aqi( data[0]);
            statistics.setCoAqi( data[1]);
            statistics.setSpmAqi( data[2]);
            statistics.setAqi( data[3]);
            statisticsMapper.update(statistics,updateWrapper);
        }
    }
}
