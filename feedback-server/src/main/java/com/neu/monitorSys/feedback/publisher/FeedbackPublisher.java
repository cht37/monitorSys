package com.neu.monitorSys.feedback.publisher;

import cn.hutool.core.bean.BeanUtil;
import com.neu.monitorSys.feedback.DTO.AqiFeedbackDTO;
import com.neu.monitorSys.feedback.DTO.AreaMQDTO;
import com.neu.monitorSys.feedback.client.UserClient;
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

    public boolean sendFeedback(AqiFeedbackDTO feedback){
        try {
            rabbitTemplate.convertAndSend("feedbackExchange","topic.feedback",feedback);
            AreaMQDTO areaMQDTO =new AreaMQDTO();
            BeanUtil.copyProperties(feedback,areaMQDTO);
            //判断是否有字段为空
            if(areaMQDTO.getProvinceName()==null||areaMQDTO.getCityName()==null||areaMQDTO.getDistrictName()==null){
                return false;
            }
            rabbitTemplate.convertAndSend("feedbackExchange","topic.area",areaMQDTO);
        } catch (AmqpException e) {
            return false;
        }
        return true;

    }

    //指派给区域管理员，异步请求，停用
//    public boolean sendAssignTASK(AssignDTO assignDTO){
//        try {
//            rabbitTemplate.convertAndSend("assignExchange","topic.assign",assignDTO);
//        } catch (AmqpException e) {
//            return false;
//        }
//        return true;
//    }
}
