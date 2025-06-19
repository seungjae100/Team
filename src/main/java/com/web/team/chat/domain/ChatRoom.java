package com.web.team.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
