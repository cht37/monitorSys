package com.neu.monitorSys.DTO;

import org.springframework.context.ApplicationEvent;

/**
 * 事件消息体
 */


public class MsgEvent extends ApplicationEvent {
    /**
     * 客户端id
     */
    private String clientId;
    /**
     * 传输数据体(json)
     */
    private String message;

    public MsgEvent(Object source, String clientId, String message) {
        super(source);
        this.clientId = clientId;
        this.message = message;
    }

    public MsgEvent(Object source,String message) {
        super(source);
        clientId=null;
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
