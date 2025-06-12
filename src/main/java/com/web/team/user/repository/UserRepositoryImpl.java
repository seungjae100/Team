package com.web.team.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.web.team.user.domain.User;
import lombok.RequiredArgsConstructor;
import com.web.team.user.domain.QUser;

import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QUser user = QUser.user;

    @Override
    public Optional<User> findActiveUserByEmail(String email) {

        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(user)
                        .where(
                                user.email.eq(email),
                                user.isActive.eq(true)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Optional<User> findAnyUserByEmail(String email) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(user)
                        .where(user.email.eq(email))
                        .fetchOne()
        );
    }
}
