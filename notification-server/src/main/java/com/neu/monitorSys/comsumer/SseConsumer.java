package com.neu.monitorSys.comsumer;

import com.neu.monitorSys.DTO.MsgEvent;
import com.neu.monitorSys.common.config.RabbitMQCommonConfig;
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
            exchange = @Exchange(value = RabbitMQCommonConfig.FEEDBACK_NOTIFICATION_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = {"msg.feedback"}
    ))
    @RabbitHandler
    public void process(String message) {
         // 消息格式为 "message" json字符串
        //将消息发布到所有监听反馈更新状况的客户端
       applicationContext.publishEvent(new MsgEvent(this, message));

    }

}
