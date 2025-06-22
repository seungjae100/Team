package com.web.team.chat.repository;

import com.web.team.chat.domain.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long>, ChatParticipantRepositoryCustom {

}
