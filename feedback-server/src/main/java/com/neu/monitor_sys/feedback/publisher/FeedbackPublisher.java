package com.neu.monitor_sys.feedback.publisher;

import cn.hutool.core.bean.BeanUtil;
import com.neu.monitor_sys.common.config.RabbitMQCommonConfig;
import com.neu.monitor_sys.feedback.DTO.AqiFeedbackDTO;
import com.neu.monitor_sys.feedback.DTO.AreaMQDTO;
import com.neu.monitor_sys.feedback.client.UserClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserClient userClient;

    /**
     * 发送反馈信息到消息队列
     * @param feedback 反馈信息
     * @return 是否发送成功
     */
    public boolean sendFeedback(AqiFeedbackDTO feedback){
        try {
            rabbitTemplate.convertAndSend(RabbitMQCommonConfig.FEEDBACK_EXCHANGE,"topic.feedback",feedback);
            AreaMQDTO areaMQDTO =new AreaMQDTO();
            BeanUtil.copyProperties(feedback,areaMQDTO);
            //判断是否有字段为空
            if(areaMQDTO.getProvinceName()==null||areaMQDTO.getCityName()==null||areaMQDTO.getDistrictName()==null){
                return false;
            }
            rabbitTemplate.convertAndSend(RabbitMQCommonConfig.FEEDBACK_EXCHANGE,"topic.area",areaMQDTO);
        } catch (AmqpException e) {
            return false;
        }
        return true;

    }

    /**
     * 发送反馈刷新通知
     *
     * @param clientId 用户id
     * @param msg      通知内容
     */
    public void sendFeedbackNotify(String msg) {
        try {
            //向正在监听的用户发送消息
            rabbitTemplate.convertAndSend(RabbitMQCommonConfig.NOTIFICATION_EXCHANGE, "msg.feedback",   msg);
        } catch (AmqpException e) {
            throw new RuntimeException("发送消息失败");
        }
    }
}
