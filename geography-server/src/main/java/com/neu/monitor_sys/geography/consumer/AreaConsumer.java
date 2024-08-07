package com.neu.monitor_sys.geography.consumer;

import com.neu.monitor_sys.common.config.RabbitMQCommonConfig;
import com.neu.monitor_sys.geography.DTO.AreaMQDTO;
import com.neu.monitor_sys.geography.service.IGridManagerAreaService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class AreaConsumer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private IGridManagerAreaService gridManagerAreaService;

//    /**
//     * 接收区域信息，写入数据库
//     * @param area
//     */
    @RabbitListener(queues = RabbitMQCommonConfig.AREA_QUEUE,errorHandler = "rabbitMQErrorHandler")
    @Transactional
    public void receiveArea(AreaMQDTO area) {
        Boolean aBoolean = gridManagerAreaService.saveAreaByMQ(area);
        if (aBoolean) {
            log.info("区域信息自动存储成功");
        } else {
            log.info("区域信息自动存储失败");
        }
    }
}
