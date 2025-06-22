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
public class ChatParticipant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 사용자 아이디

    private boolean exited; // 퇴장 참/거짓
    private LocalDateTime enteredAt; // 입장시간
    private LocalDateTime exitedAt; // 퇴장시간

    public static ChatParticipant enter(ChatRoom chatRoom, User user) {
        ChatParticipant p = new ChatParticipant();
        chatRoom.addParticipant(p);
        user.addParticipant(p);
        p.enteredAt = LocalDateTime.now();
        p.exited = false; // 초기값 : 나가지 않은 상태
        return p;
    }

    public void exit() {
        this.exited = true;
        this.exitedAt = LocalDateTime.now();
    }

    public void reEnter() {
        this.exited = false; // 퇴장 상태를 해제
        this.enteredAt = LocalDateTime.now(); // 입장시간 지금을 기록
        this.exitedAt = null; // 기존 퇴장시간을 null로 처리
    }

    public void assignChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void assignUser(User user) {
        this.user = user;
    }
}
