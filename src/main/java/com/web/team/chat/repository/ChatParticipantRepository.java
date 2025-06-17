package com.web.team.chat.repository;

import com.web.team.chat.domain.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long>, ChatParticipantRepositoryCustom {

    // 채팅방에 참여한 사용자들을 찾는 메서드
    Optional<ChatParticipant> findByRoomIdAndUserId(Long roomId, Long userId);

    void deleteByRoomId(Long roomId);
}
