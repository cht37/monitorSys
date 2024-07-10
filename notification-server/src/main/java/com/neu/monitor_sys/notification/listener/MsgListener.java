package com.neu.monitor_sys.notification.listener;

import com.neu.monitor_sys.notification.event.FeedbackRefreshEvent;
import com.neu.monitor_sys.notification.event.LogoutEvent;
import com.neu.monitor_sys.notification.service.impl.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MsgListener {
     @Autowired
    private NotificationServiceImpl notificationService;


    @EventListener
    public void deployEventHandler(FeedbackRefreshEvent feedbackRefreshEvent) {
        String message = feedbackRefreshEvent.getMessage();
        notificationService.sendMsgToFeedback(message);
    }

    /**
     * 监听登出事件
     * @param logoutEvent
     */
    @EventListener
    public void logoutEventHandler(LogoutEvent logoutEvent) {
      notificationService.sendMessageToOneClient(logoutEvent.getClientId(), logoutEvent.getMessage());
    }
}
