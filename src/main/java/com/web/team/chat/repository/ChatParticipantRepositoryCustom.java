package com.web.team.chat.repository;

import com.web.team.chat.domain.ChatParticipant;

import java.util.Optional;

public interface ChatParticipantRepositoryCustom {

    // 모든 사람들이 채팅방에서 나갔는지 확인
    boolean allUserExited(Long roomId);

    // 채팅방에 참여한 사용자들을 찾는 메서드
    Optional<ChatParticipant> findByRoomIdAndUserId(Long roomId, Long userId);
}
