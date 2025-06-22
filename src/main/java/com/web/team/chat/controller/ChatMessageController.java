package com.web.team.chat.controller;

import com.web.team.chat.dto.ChatMessageRequest;
import com.web.team.chat.dto.ChatMessageResponse;
import com.web.team.chat.service.ChatMessageService;
import com.web.team.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    // 채팅 메세지 전송
    @PostMapping
    public ChatMessageResponse sendMessage(@RequestBody ChatMessageRequest request,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        return chatMessageService.sendMessage(request, userDetails);
    }

    // 특정 채팅 메세지 조회
    @GetMapping("/{roomId}")
    public List<ChatMessageResponse> getMessages(@PathVariable Long roomId) {
        return chatMessageService.getMessageByRoomId(roomId);
    }

}
