package com.neu.monitor_sys.feedback.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMQConfig {
//    //反馈信息交换机
//    public static final String FEEDBACK_EXCHANGE = "feedback_exchange";
//    //反馈通知交换机
//    public static final String NOTIFICATION_EXCHANGE = "feedback_notification_exchange";
//    //反馈信息队列
//    public static final String FEEDBACK_QUEUE = "feedback_queue";
//    //区域信息队列
//    public static final String AREA_QUEUE = "area_queue";
//    /**
//     * 配置交换机，反馈信息交换机，用于接收反馈信息
//     * @return
//     */
//    @Bean(FEEDBACK_EXCHANGE)
//    public TopicExchange feedbackExchange(){
//        // 三个参数：交换机名称、是否持久化、当没有queue与其绑定时是否自动删除
//        return new TopicExchange(FEEDBACK_EXCHANGE,true,false);
//    }
//
//    /**
//     *  反馈通知交换机
//     *  FeedbackNotificationExchange
//     * @return TopicExchange
//     */
//    @Bean(NOTIFICATION_EXCHANGE)
//    public TopicExchange feedbackNotificationExchange(){
//        // 三个参数：交换机名称、是否持久化、当没有queue与其绑定时是否自动删除
//        return new TopicExchange(NOTIFICATION_EXCHANGE,true,false);
//    }
//
//    /**
//     * 配置队列，反馈信息队列
//     * @return
//     */
//    @Bean(FEEDBACK_QUEUE)
//    public Queue feedbackQueue(){
//        return QueueBuilder.durable(FEEDBACK_QUEUE).build();
//    }
//    /**
//     * 配置队列，写入区域信息队列
//     * @return
//     */
//    @Bean(AREA_QUEUE)
//    public Queue areaQueue(){
//        return QueueBuilder.durable(AREA_QUEUE).build();
//    }
//
//
//    //绑定反馈队列
//    @Bean
//    public Binding bindingTopic1(@Qualifier(FEEDBACK_QUEUE) Queue queue, @Qualifier(FEEDBACK_EXCHANGE) TopicExchange topicExchange){
//        return BindingBuilder.bind(queue).to(topicExchange).with("topic.feedback");
//    }
//    //绑定写入区域消息队列
//    @Bean
//    public Binding bindingTopic2(@Qualifier(AREA_QUEUE) Queue queue, @Qualifier(FEEDBACK_EXCHANGE   ) TopicExchange topicExchange){
//        return BindingBuilder.bind(queue).to(topicExchange).with("topic.area");
//    }

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
