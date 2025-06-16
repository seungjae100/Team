package com.web.team.chat.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.web.team.chat.domain.QChatParticipant;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChatParticipantRepositoryImpl implements ChatParticipantRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean allUserExited(Long roomId) {
        QChatParticipant participant = QChatParticipant.chatParticipant;

        Long count = jpaQueryFactory
                .select(participant.count())
                .from(participant)
                .where(participant.roomId.eq(roomId)
                        .and(participant.exited.isFalse()))
                .fetchOne();

        return count == 0;
    }
}
