package com.neu.monitor_sys.common.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQCommonConfig {
    //反馈信息交换机
    public static final String FEEDBACK_EXCHANGE = "feedback_exchange";
    //反馈通知交换机
    public static final String NOTIFICATION_EXCHANGE = "notification_exchange";
    //统计信息交换机
    public static final String STATISTICS_EXCHANGE = "statistics_exchange";
    //死信交换机
    public static final String DEAD_LETTER_EXCHANGE = "dead_letter_exchange";
    //--------------------------------------------------------------Queue-------------------------------------------------------------------
    //反馈信息队列
    public static final String FEEDBACK_QUEUE = "feedback_queue";
    //区域信息队列
    public static final String AREA_QUEUE = "area_queue";
    //统计数据队列
    public static final String DATA_QUEUE = "data_queue";
    //死信队列
    public static final String DEAD_LETTER_QUEUE = "dead_letter_queue";
    //强制登出队列
    public static final String LOGOUT_QUEUE = "logout_queue";
    //--------------------------------------------------------------Exchange-------------------------------------------------------------------

    /**
     * 配置交换机，反馈信息交换机，用于接收反馈信息
     *
     * @return
     */
    @Bean(FEEDBACK_EXCHANGE)
    public TopicExchange feedbackExchange() {
        // 三个参数：交换机名称、是否持久化、当没有queue与其绑定时是否自动删除
        return new TopicExchange(FEEDBACK_EXCHANGE, true, false);
    }

    /**
     * 反馈通知交换机
     * FeedbackNotificationExchange
     *
     * @return TopicExchange
     */
    @Bean(NOTIFICATION_EXCHANGE)
    public TopicExchange feedbackNotificationExchange() {
        // 三个参数：交换机名称、是否持久化、当没有queue与其绑定时是否自动删除
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    /**
     * 配置交换机，统计信息交换机，用于接收统计信息
     *
     * @return FanoutExchange
     */
    @Bean(STATISTICS_EXCHANGE)
    public FanoutExchange statisticsExchange() {
        return new FanoutExchange(STATISTICS_EXCHANGE, true, false);
    }

    /**
     * 配置交换机，死信交换机，用于接收死信信息
     */
    @Bean(DEAD_LETTER_EXCHANGE)
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }
    //--------------------------------------------------------------Queue-------------------------------------------------------------------

    /**
     * 配置队列，反馈信息队列
     *
     * @return
     */
    @Bean(FEEDBACK_QUEUE)
    public Queue feedbackQueue() {
        return QueueBuilder.durable(FEEDBACK_QUEUE).withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dead.letter").build();
    }

    /**
     * 配置队列，写入区域信息队列
     *
     * @return
     */
    @Bean(AREA_QUEUE)
    public Queue areaQueue() {
        return QueueBuilder.durable(AREA_QUEUE).withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dead.letter").build();
    }

    /**
     * 配置队列，数据队列
     *
     * @return
     */
    @Bean(DATA_QUEUE)
    public Queue dataQueue(){
        return QueueBuilder.durable(DATA_QUEUE).build();
    }

    /**
     * 配置队列，死信队列
     */
    @Bean(DEAD_LETTER_QUEUE)
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dead.letter").build();
    }



    //----------------------------------------------Binding-----------------------------------------------------------------------------------
    //绑定反馈队列
    @Bean
    public Binding bindingTopic1(@Qualifier(FEEDBACK_QUEUE) Queue queue, @Qualifier(FEEDBACK_EXCHANGE) TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with("topic.feedback");

    }

    //绑定写入区域消息队列
    @Bean
    public Binding bindingTopic2(@Qualifier(AREA_QUEUE) Queue queue, @Qualifier(FEEDBACK_EXCHANGE) TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with("topic.area");
    }

    /**
     * 绑定指派任务队列
     *
     * @return Binding
     */
    @Bean
    public Binding bindingTopic3(@Qualifier(DATA_QUEUE) Queue queue, @Qualifier(STATISTICS_EXCHANGE) FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }
    /**
     * 绑定死信队列
     */
    @Bean
    public Binding bindingDeadLetterQueue(@Qualifier(DEAD_LETTER_QUEUE) Queue queue, @Qualifier(DEAD_LETTER_EXCHANGE) DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with("dead.letter");
    }

//    //配置消息转换器
//    @Bean
//    public static MessageConverter messageConverter(){
//        return new Jackson2JsonMessageConverter();
//    }


}
