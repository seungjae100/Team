package com.web.team.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.web.team.chat.domain.ChatRoom;
import com.web.team.chat.domain.QChatParticipant;
import com.web.team.chat.domain.QChatRoom;
import com.web.team.chat.domain.RoomType;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ChatRoom> findDirectRoom(Long userId1, Long userId2) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QChatParticipant chatParticipant = QChatParticipant.chatParticipant;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(chatRoom)
                        .where(
                                chatRoom.roomType.eq(RoomType.DIRECT),
                                chatRoom.id.in(
                                        queryFactory
                                                .select(chatParticipant.chatRoom.id)
                                                .from(chatParticipant)
                                                .where(chatParticipant.user.id.in(userId1, userId2))
                                                .groupBy(chatParticipant.chatRoom.id)
                                                .having(chatParticipant.chatRoom.id.count().eq(2L))
                                )
                        )
                        .fetchOne()
        );
    }
}
