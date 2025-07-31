package com.web.team.chat.controller;

import com.web.team.chat.dto.ChatMessageRequest;
import com.web.team.chat.dto.ChatMessageResponse;
import com.web.team.chat.dto.MessageLoadRequest;
import com.web.team.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    /*
    * 클라이언트로부터 /app/chat.sendMessage로 메세지 수신
    * 저장 후 /topic/chat/room/{roomId} 로 브로드캐스트
    * */

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest request,
                            @Header("simpSessionAttributes") java.util.Map<String, Object> attributes) {

        Long userId = (Long) attributes.get("userId");

        ChatMessageResponse response = chatMessageService.sendMessage(request, userId);


        messagingTemplate.convertAndSend("/topic/chat/" + request.getRoomId(), response);
    }

    @MessageMapping("/chat.loadMessages")
    public void loadMessages(@Payload MessageLoadRequest request,
                             @Header("simpSessionAttributes")Map<String, Object> attributes) {

        Long userId = (Long) attributes.get("userId");

        List<ChatMessageResponse> messages = chatMessageService.getMessageByRoomId(
             request.getRoomId(), userId, request.getPage(), request.getSize()
        );

        messagingTemplate.convertAndSend("/topic/chat/" + request.getRoomId() + "/history", messages);
    }
}
