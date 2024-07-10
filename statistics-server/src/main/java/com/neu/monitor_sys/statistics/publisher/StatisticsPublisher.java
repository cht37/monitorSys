package com.neu.monitor_sys.statistics.publisher;

import com.neu.monitor_sys.common.config.RabbitMQCommonConfig;
import com.neu.monitor_sys.statistics.DTO.ReportDTO;
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
