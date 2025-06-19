package com.web.team.chat.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;
    private Long senderId;
    private String senderName;
    private String message;
    private LocalDateTime sentAt;

    public static ChatMessage create(Long roomId, Long senderId, String senderName, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.roomId = roomId;
        chatMessage.senderId = senderId;
        chatMessage.senderName = senderName;
        chatMessage.message = message;
        chatMessage.sentAt = LocalDateTime.now();
        return chatMessage;
    }
}
