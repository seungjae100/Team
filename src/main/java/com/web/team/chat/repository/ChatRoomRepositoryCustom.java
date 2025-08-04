package com.web.team.chat.repository;

import com.web.team.chat.domain.ChatRoom;

import java.util.Optional;

public interface ChatRoomRepositoryCustom {

    Optional<ChatRoom> findDirectRoom(Long userId1, Long userId2);
}
