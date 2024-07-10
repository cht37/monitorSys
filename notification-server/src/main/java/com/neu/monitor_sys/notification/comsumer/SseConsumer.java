package com.neu.monitor_sys.notification.comsumer;

import cn.hutool.json.JSONUtil;
import com.neu.monitor_sys.common.DTO.LogoutMsg;
import com.neu.monitor_sys.notification.event.FeedbackRefreshEvent;
import com.neu.monitor_sys.common.config.RabbitMQCommonConfig;
import com.neu.monitor_sys.notification.event.LogoutEvent;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SseConsumer {
     private String mdlMsg = "";
    public String getMdlMsg() { return mdlMsg; }

     @Autowired
     ApplicationContext applicationContext;
    //监听更新消息队列
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "update_msg_queue", durable = "true",autoDelete = "true"),
            exchange = @Exchange(value = RabbitMQCommonConfig.NOTIFICATION_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = {"msg.feedback"}
    ))
    @RabbitHandler
    public void process(String message) {
         // 消息格式为 "message" json字符串
        //将消息发布到所有监听反馈更新状况的客户端
       applicationContext.publishEvent(new FeedbackRefreshEvent(this, message));

    }

    //监听强制登出消息队列
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "logout_msg_queue", durable = "true",autoDelete = "true"),
            exchange = @Exchange(value = RabbitMQCommonConfig.NOTIFICATION_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = {"msg.logout"}
    ))
    @RabbitHandler
    public void processLogout(String message) {
        // 消息格式为 "message" json字符串
        LogoutMsg logoutMsg = JSONUtil.toBean(message, LogoutMsg.class);
        //将消息发布到对应所有监听强制登出的客户端
        String clientId="logout:"+logoutMsg.getLogId();
        applicationContext.publishEvent(new LogoutEvent(this,clientId,logoutMsg.getMessage()));

    }


}
