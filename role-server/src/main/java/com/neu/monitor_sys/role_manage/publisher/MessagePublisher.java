package com.neu.monitor_sys.role_manage.publisher;

import com.neu.monitor_sys.common.config.RabbitMQCommonConfig;
import com.neu.monitor_sys.common.DTO.LogoutMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessagePublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送退出登录的消息到消息队列
     */
    public void sendLogoutMessage(LogoutMsg msg) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQCommonConfig.NOTIFICATION_EXCHANGE, "msg.logout", msg);
        } catch (Exception e) {
            log.error("发送退出登录消息失败", e);
        }
    }
}
