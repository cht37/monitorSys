package com.neu.monitorSys.feedback.consumer;

import com.neu.monitorSys.feedback.DTO.AqiFeedbackDTO;
import com.neu.monitorSys.feedback.service.IAqiFeedbackService;
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
    @RabbitListener(queues = "feedbackQueue",errorHandler = "rabbitMQErrorHandler")
    public void receiveFeedback(AqiFeedbackDTO feedback){
        aqiFeedbackService.saveFeedback(feedback);
    }
}
