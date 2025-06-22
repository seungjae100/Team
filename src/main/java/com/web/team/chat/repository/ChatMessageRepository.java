package com.web.team.chat.repository;

import com.web.team.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageRepositoryCustom {

}
