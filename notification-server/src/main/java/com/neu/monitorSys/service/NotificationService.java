package com.neu.monitorSys.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
     /**
     * 创建连接
     *
     * @param clientId 客户端ID
     */
    SseEmitter createSseEmitter(String clientId);

     /**
     * 给指定客户端发送消息
     *
     * @param clientId 客户端ID
     * @param msg      消息内容
     */
    void sendMessageToOneClient(String clientId, String msg);
     /**
     * 关闭连接
     *
     * @param clientId 客户端ID
     */
    void closeConnect(String clientId);
    /**
     * 给反馈模块发送消息
     */
    void sendMsgToFeedback(String msg);
}
