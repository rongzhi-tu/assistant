package com.bsoft.assistant.controller;

import com.bsoft.assistant.model.vo.message.Authentication;
import com.bsoft.assistant.model.vo.message.MessageResult;
import com.bsoft.assistant.model.vo.message.MessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

/**
 * Description:
 * Author: tuz
 * Date: 2023/09/02
 */
@Controller
public class WebsocketController {

    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    public WebsocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/demo/hello/{typeId}")
    @SendTo("/topic/demo")
    public MessageResult greeting(MessageVo message, StompHeaderAccessor headerAccessor) {
        Authentication user = (Authentication) headerAccessor.getUser();
        String sessionId = headerAccessor.getSessionId();
        return MessageResult.successResult(user.getName(), "sessionId: " + sessionId + ", message: " + message.getMessage());
    }

    @MessageMapping("/test/send/{destUsername}")
    @SendToUser("/test")
    public MessageResult testToUser(@DestinationVariable String destUsername, StompHeaderAccessor headerAccessor) {
        Authentication user = (Authentication) headerAccessor.getUser();
        String sessionId = headerAccessor.getSessionId();
        MessageVo msg = MessageVo.as(destUsername, "sessionId: " + sessionId + ", message: 测试消息");
        // 对目标进行发送信息
        messagingTemplate.convertAndSendToUser(destUsername, "/test", msg);
        return MessageResult.successResult("系统" +  new Date().toString() + "消息已被推送。");
    }
}
