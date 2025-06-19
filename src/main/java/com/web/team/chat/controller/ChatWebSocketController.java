package com.web.team.chat.controller;

import com.web.team.chat.dto.ChatMessageRequest;
import com.web.team.chat.dto.ChatMessageResponse;
import com.web.team.chat.service.ChatMessageService;
import com.web.team.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

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
    public void sendMessage(ChatMessageRequest request,
                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 메세지 저장
        ChatMessageResponse response = chatMessageService.sendMessage(request, userDetails);

        // 구독자들에게 메세지 전송
        messagingTemplate.convertAndSend("/topic/chat/room/" + request.getRoomId(), response);
    }
}
