package com.web.team.chat.dto;

import com.web.team.chat.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {

    private Long roomId;
    private Long senderId;
    private String senderName;
    private String message;
    private LocalDateTime sentAt;

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getChatRoom().getId(),
                chatMessage.getSender().getId(),
                chatMessage.getSender().getName(),
                chatMessage.getMessage(),
                chatMessage.getSentAt()
        );
    }
}
