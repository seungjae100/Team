package com.web.team.chat.repository;

import com.web.team.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 roomId를 가진 모든 채팅 메세지 일괄 삭제
    void deleteByRoomId(Long roomId);

    // 특정 채팅방의 메세지를 보낸 시간 기준으로 오름차순 정렬해서 모두 조회
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(Long roomId);

}
