package com.web.team.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.web.team.chat.domain.ChatParticipant;
import com.web.team.chat.domain.QChatParticipant;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class ChatParticipantRepositoryImpl implements ChatParticipantRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean allUserExited(Long roomId) {
        QChatParticipant participant = QChatParticipant.chatParticipant;

        Long count = jpaQueryFactory
                .select(participant.count())
                .from(participant)
                .where(participant.chatRoom.id.eq(roomId)
                        .and(participant.exited.isFalse()))
                .fetchOne();

        return count == 0;
    }

    @Override
    public Optional<ChatParticipant> findByRoomIdAndUserId(Long roomId, Long userId) {
        QChatParticipant participant = QChatParticipant.chatParticipant;

        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(participant)
                        .where(participant.chatRoom.id.eq(roomId)
                                .and(participant.user.id.eq(userId)))
                        .fetchOne()
        );
    }
}
