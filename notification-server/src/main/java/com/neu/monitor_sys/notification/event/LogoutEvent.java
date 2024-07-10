package com.neu.monitor_sys.notification.event;

import org.springframework.context.ApplicationEvent;

/**
 * 事件消息体
 */


public class LogoutEvent extends ApplicationEvent {
    /**
     * 客户端id
     */
    private String clientId;
    /**
     * 传输数据体(json)
     */
    private String message;

    public LogoutEvent(Object source, String clientId, String message) {
        super(source);
        this.clientId = clientId;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
