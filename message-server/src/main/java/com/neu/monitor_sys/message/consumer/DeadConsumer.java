package com.neu.monitor_sys.message.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class DeadConsumer {
      @RabbitListener(queues = "dead_letter_queue")
    public void processDeadLetterQueue(String message) {
        // 处理死信队列中的消息
        System.out.println("Received dead letter message: " + message);
        // 这里可以添加逻辑，例如记录日志、发送报警、重试处理等
    }
}
