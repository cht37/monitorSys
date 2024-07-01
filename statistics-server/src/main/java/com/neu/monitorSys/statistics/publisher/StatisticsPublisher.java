package com.neu.monitorSys.statistics.publisher;

import com.neu.monitorSys.common.config.RabbitMQCommonConfig;
import com.neu.monitorSys.statistics.DTO.ReportDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送统计数据
     * @param reportDTO
     * @return
     */
    public boolean sendStaticsData(ReportDTO reportDTO){
        try {
            rabbitTemplate.convertAndSend(RabbitMQCommonConfig.STATISTICS_EXCHANGE,"",reportDTO);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
