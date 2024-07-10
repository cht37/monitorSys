package com.neu.monitor_sys.notification.controller;

import com.neu.monitor_sys.common.config.RabbitMQCommonConfig;
import com.neu.monitor_sys.notification.service.impl.NotificationServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notification")
@CrossOrigin(origins = "*", maxAge = 3600)
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
    @GetMapping(path = "/logout/connect")
    public SseEmitter createLogoutSseEmitter(@RequestParam String userId){
        String clientId = "logout:"+userId;
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
     * 关闭feedback连接
     * @param logId 用户Id
     */
    @GetMapping(path = "/feedback/close")
    public void closeFeedbackConnect(@RequestParam String logId) {
        String clientId = "feedback:"+logId;
        notificationService.closeConnect(clientId);
    }
        /**
     * 关闭feedback连接
     * @param logId 用户Id
     */
    @GetMapping(path = "/logout/close")
    public void closeLogoutConnect(@RequestParam String logId) {
        String clientId = "logout:"+logId;
        notificationService.closeConnect(clientId);
    }
    @GetMapping
    public void sendMessageToOneClient(@RequestParam String msg) {
        rabbitTemplate.convertAndSend(RabbitMQCommonConfig.NOTIFICATION_EXCHANGE,"msg.feedback", msg);
    }

}

