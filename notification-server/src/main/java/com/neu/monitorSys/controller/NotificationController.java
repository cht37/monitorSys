package com.neu.monitorSys.controller;

import com.neu.monitorSys.common.config.RabbitMQCommonConfig;
import com.neu.monitorSys.service.impl.NotificationServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notification")
@CrossOrigin
public class NotificationController {


    @Autowired
    private RabbitTemplate rabbitTemplate;
   @Autowired
    private NotificationServiceImpl notificationService;

    /**
     * 创建sse连接
     * @param logId 用户Id
     * @return SseEmitter
     */
    @GetMapping(path = "/connect")
    public SseEmitter createSseEmitter(@RequestParam String clientId){
        return notificationService.createSseEmitter(clientId);
    }

    /**
     * 创建反馈sse连接
     * @param userId 用户Id
     * @return SseEmitter
     */
    @GetMapping(path = "/feedback/connect")
    public SseEmitter createFeedbackSseEmitter(@RequestParam String userId) {
        String clientId = "feedback:"+userId;
        return notificationService.createSseEmitter(clientId);
    }

    /**
     * 关闭连接
     * @param logId 用户Id
     */
    @GetMapping(path = "/close")
    public void closeConnect(@RequestParam String logId) {
        notificationService.closeConnect(logId);
    }

    @GetMapping
    public void sendMessageToOneClient(@RequestParam String msg) {
        rabbitTemplate.convertAndSend(RabbitMQCommonConfig.FEEDBACK_NOTIFICATION_EXCHANGE,"msg.feedback", msg);
    }

}

