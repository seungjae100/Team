package com.web.team.schedule.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.web.team.admin.domain.Admin;
import com.web.team.schedule.domain.QSchedule;
import com.web.team.schedule.domain.Schedule;
import com.web.team.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleQueryRepositoryImpl implements ScheduleQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QSchedule schedule = QSchedule.schedule;

    @Override
    public List<Schedule> findAllForEmployee(User user) {
        return jpaQueryFactory
                .selectFrom(schedule)
                .where(
                        schedule.is_company.isTrue()
                                .or(schedule.user.eq(user))

                )
                .fetch();
    }

    @Override
    public List<Schedule> findAllForAdmin(Admin admin) {
        return jpaQueryFactory
                .selectFrom(schedule)
                .where(
                        schedule.is_company.isTrue()
                                .and(schedule.admin.eq(admin))
                )
                .fetch();
    }

    @Override
    public Optional<Schedule> findByIdForAdmin(Long scheduleId, Admin admin) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(schedule)
                .where(
                        schedule.id.eq(scheduleId)
                                .and(schedule.is_company.isTrue())
                                .and(schedule.admin.eq(admin))
                )
                .fetchOne());
    }

    @Override
    public Optional<Schedule> findByIdForEmployee(Long scheduleId, User user) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(schedule)
                .where(
                        schedule.id.eq(scheduleId)
                                .and(schedule.is_company.isTrue()
                                        .or(schedule.user.eq(user)))
                )
                .fetchOne());
    }
}
