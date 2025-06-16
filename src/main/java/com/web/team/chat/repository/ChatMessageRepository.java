package com.web.team.chat.repository;

import com.web.team.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 삭제된 채팅방 조회
    void deleteByRoomId(Long roomId);

    // 채팅방의 메세지 조회
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(Long roomId);

}
