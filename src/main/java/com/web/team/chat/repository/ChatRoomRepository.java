package com.web.team.chat.repository;

import com.web.team.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 1:1 채팅방에 조회 중복 없는 유저들을 조회
    Optional<ChatRoom> findByDirectRoomByUsers(Long currentUserId, Long anotherId);
}
