package com.neu.monitorSys.listener;

import com.neu.monitorSys.DTO.MsgEvent;
import com.neu.monitorSys.service.impl.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MsgListener {
     @Autowired
    private NotificationServiceImpl notificationService;


    @EventListener
    public void deployEventHandler(MsgEvent msgEvent) throws IOException {
        String message = msgEvent.getMessage();
        notificationService.sendMsgToFeedback(message);
    }


}
