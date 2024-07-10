package com.neu.monitor_sys.feedback.consumer;

import com.neu.monitor_sys.common.config.RabbitMQCommonConfig;
import com.neu.monitor_sys.feedback.DTO.AqiFeedbackDTO;
import com.neu.monitor_sys.feedback.service.IAqiFeedbackService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackConsumer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private IAqiFeedbackService aqiFeedbackService;
    @RabbitListener(queues = RabbitMQCommonConfig.FEEDBACK_QUEUE,errorHandler = "rabbitMQErrorHandler")
    public void receiveFeedback(AqiFeedbackDTO feedback){
        aqiFeedbackService.saveFeedback(feedback);
    }
}
