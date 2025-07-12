package com.web.team.schedule.repository;

import com.web.team.admin.domain.Admin;
import com.web.team.schedule.domain.Schedule;
import com.web.team.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface ScheduleQueryRepository {

    // 직원이 등록한 전체 일정 조회
    List<Schedule> findAllForEmployee(User user);

    // 관리자가 등록한 전체 회사 일정 조회
    List<Schedule> findAllForAdmin(Admin admin);

    // 관리자가 등록한 회사일정 중 한 가지 상세 조회
    Optional<Schedule> findByIdForAdmin(Long scheduleId, Admin admin);

    // 직원이 등록한 일정 중 한 가지 상세 조회
    Optional<Schedule> findByIdForEmployee(Long scheduleId, User user);


}
