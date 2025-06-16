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
public class ChatParticipant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId; // 채팅방 아이디
    private Long userId; // 사용자 아이디

    private boolean exited; // 퇴장 참/거짓
    private LocalDateTime enteredAt; // 입장시간
    private LocalDateTime exitedAt; // 퇴장시간

    public static ChatParticipant enter(Long roomId, Long userId) {
        ChatParticipant p = new ChatParticipant();
        p.roomId = roomId;
        p.userId = userId;
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
}
