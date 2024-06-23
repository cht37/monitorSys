package com.neu.monitorSys.geography.handler;

import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component("rabbitMQErrorHandler")
public class CustomRabbitMQErrorHandler implements RabbitListenerErrorHandler {


    @Override
    public Object handleError(org.springframework.amqp.core.Message message, Message<?> message1, ListenerExecutionFailedException e) throws Exception {
          // 记录错误日志
        System.err.println("Message: " + message);
        System.err.println("Exception: " + e.getMessage());

        // 发送告警通知（示例，实际需要集成告警系统）
        sendAlert(message1, e);

        // 根据需求决定是否重新抛出异常或进行其他处理
        // throw exception;
        return null;  // 返回null表示不再处理该消息
    }

    private void sendAlert(Message<?> message, ListenerExecutionFailedException exception) {
        // 实现告警通知逻辑，比如发送邮件或短信
        System.out.println("Sending alert for message: " + message);
    }
}
