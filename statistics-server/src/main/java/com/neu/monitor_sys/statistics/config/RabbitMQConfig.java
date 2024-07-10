package com.neu.monitor_sys.statistics.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
//    /**
//     * 配置交换机，反馈信息交换机，用于接收反馈信息
//     * @return
//     */
//    @Bean
//    public FanoutExchange statisticsExchange(){
//        return new FanoutExchange("statisticsExchange");
//    }

//    /**
//     * 配置队列，数据队列
//     * @return
//     */
//    @Bean
//    public Queue dataQueue(){
//        return new Queue("dataQueue");
//    }
//TODO 配置死信队列

//    /**
//     * 配置死信队列
//     */
//    @Bean
//    public Queue deadQueue(){
//        return new Queue("deadQueue");
//    }


    //配置消息转换器
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置rabbitTemplate
     * @param connectionFactory
     * @return
     */
     @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

}
