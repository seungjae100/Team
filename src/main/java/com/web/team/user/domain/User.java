package com.web.team.user.domain;

import com.web.team.chat.domain.ChatMessage;
import com.web.team.chat.domain.ChatParticipant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본키

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (아이디)

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String name; // 직원명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ADMIN, USER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Position position; // 임원, 부장, 과장, 팀장, 대리, 주임, 사원, 인턴

    @Column(nullable = false)
    private boolean isActive; // 계정 활성화/비활성화

    // 연관관계
    @OneToMany(mappedBy = "user")
    private List<ChatParticipant> chatParticipants = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<ChatMessage> chatMessages = new ArrayList<>();

    public void addParticipant(ChatParticipant chatParticipant) {
        this.chatParticipants.add(chatParticipant);
        chatParticipant.assignUser(this); // 양방향 동기화
    }

    public void addMessage(ChatMessage message) {
        this.chatMessages.add(message);
        message.assignSender(this);
    }

    // 관리자의 직원 등록
    public static User create(String email, String password, String name, Position position) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.role = Role.USER;
        user.position = position;
        user.name = name;
        user.isActive = true; // 기본 계정 활성화 상태 유지
        return user;
    }

    // 관리자의 직원 정보 수정
    public void updateUser(String name, Position position, Boolean isActive) {
        if (name != null && !name.isBlank()) this.name = name;
        if (position != null) this.position = position;
        if (isActive != null) this.isActive = isActive;
    }

    // 직원의 본인 비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }











}
