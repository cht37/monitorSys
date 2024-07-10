package com.neu.monitor_sys.notification.event;

import org.springframework.context.ApplicationEvent;

/**
 * 事件消息体
 */


public class FeedbackRefreshEvent extends ApplicationEvent {

    /**
     * 传输数据体(json)
     */
    private String message;


    public FeedbackRefreshEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
