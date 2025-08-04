package com.web.team.chat.domain;

import com.web.team.user.domain.User;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    private String message;
    private LocalDateTime sentAt;

    public static ChatMessage create(ChatRoom chatRoom, User sender, String message) {

        ChatMessage chatMessage = new ChatMessage();
        chatRoom.addMessage(chatMessage);
        sender.addMessage(chatMessage);
        chatMessage.message = message;
        chatMessage.sentAt = LocalDateTime.now();
        return chatMessage;
    }

    public void assignChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void assignSender(User user) {
        this.sender = user;
    }
}
