package com.web.team.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    private int userCount;

    // 연관관계
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    public void addParticipant(ChatParticipant participant) {
        this.participants.add(participant);
        participant.assignChatRoom(this);
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        message.assignChatRoom(this);
    }


    public static ChatRoom create(String name, RoomType roomType) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.name = name;
        chatRoom.roomType = roomType;
        chatRoom.userCount = 0; // 처음 채팅방 생성 시 기본 0으로 시작
        return chatRoom;
    }

    // 유저 수 증가
    public void increaseUserCount() {
        this.userCount++;
    }

    // 유저 수 감소
    public void decreaseUserCount() {
        // 만약 채팅방의 유저 수가 0 보다 크면 감소한다.
        if (this.userCount > 0) {
            this.userCount--;
        }
    }

    // 유저 수 없음 감지
    public boolean isEmpty() {
        return this.userCount == 0;
    }

}
