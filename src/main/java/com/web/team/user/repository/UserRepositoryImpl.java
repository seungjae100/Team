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

    // 로그인 ( DB에 존재하는지와 비활성화/활성화 계정인지 확인 )
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
}
