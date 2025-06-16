package com.web.team.chat.repository;

public interface ChatParticipantRepositoryCustom {

    // 모든 사람들이 채팅방에서 나갔는지 확인
    boolean allUserExited(Long roomId);
}
