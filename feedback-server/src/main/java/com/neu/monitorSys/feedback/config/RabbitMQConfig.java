package com.neu.monitorSys.feedback.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    /**
     * 配置交换机，反馈信息交换机，用于接收反馈信息
     * @return
     */
    @Bean
    public TopicExchange feedbackExchange(){
        return new TopicExchange("feedbackExchange");
    }
    /**
     * 配置交换机，指派任务交换机，用于接收指派任务
     */
    @Bean
    public TopicExchange assignExchange(){
        return new TopicExchange("assignExchange");
    }

    /**
     * 配置队列，反馈信息队列
     * @return
     */
    @Bean
    public Queue feedbackQueue(){
        return new Queue("feedbackQueue");
    }
    /**
     * 配置队列，写入区域信息队列
     * @return
     */
//    @Bean
//    public Queue areaQueue(){
//        return new Queue("areaQueue");
//    }

    /**
     * 配置队列，指派任务队列，用于接收指派任务
     * @return
     */
    @Bean
    public Queue assignTaskQueue(){
        return new Queue("assignTaskQueue");
    }
    //绑定反馈队列
    @Bean
    public Binding bindingTopic1(@Qualifier("feedbackQueue") Queue queue, @Qualifier("feedbackExchange") TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with("topic.feedback");
    }
//    //绑定写入区域消息队列
//    @Bean
//    public Binding bindingTopic2(@Qualifier("areaQueue") Queue queue, @Qualifier("feedbackExchange") TopicExchange topicExchange){
//        return BindingBuilder.bind(queue).to(topicExchange).with("topic.area");
//    }

    /**
     * 绑定指派任务队列
     * @return
     */
    @Bean
    public Binding bindingTopic3(@Qualifier("assignTaskQueue") Queue queue, @Qualifier("assignExchange") TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with("topic.assign");
    }
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
