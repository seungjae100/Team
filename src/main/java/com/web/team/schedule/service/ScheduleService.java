package com.web.team.schedule.service;

import com.web.team.admin.domain.Admin;
import com.web.team.schedule.dto.ScheduleCalendarResponse;
import com.web.team.schedule.dto.ScheduleCreateRequest;

import com.web.team.schedule.dto.ScheduleResponse;
import com.web.team.schedule.dto.ScheduleUpdateRequest;
import com.web.team.user.domain.User;

import java.util.List;

public interface ScheduleService {

    // 직원이 개인 일정을 등록
    void createEmployeeSchedule(ScheduleCreateRequest request, User user);

    // 관리자가 회사 일정을 등록
    void createAdminSchedule(ScheduleCreateRequest request, Admin admin);

    // 직원이 개인 전체 일정을 조회
    List<ScheduleCalendarResponse> getSchedulesByEmployee(User user);

    // 관리자가 회사 전체 일정을 조회
    List<ScheduleCalendarResponse> getSchedulesByAdmin(Admin admin);

    // 직원이 개인 일정을 상세 조회
    ScheduleResponse getScheduleForEmployee(Long scheduleId, User user);

    // 관리자가 회사일정을 상세 조회
    ScheduleResponse getScheduleForAdmin(Long scheduleId, Admin admin);

    // 직원 개인 스케줄의 수정
    void updateEmployeeSchedule(Long scheduleId, User user, ScheduleUpdateRequest request);

    // 회사 일정 수정
    void updateAdminSchedule(Long scheduleId, Admin admin, ScheduleUpdateRequest request);

    // 회사 일정 삭제
    void deleteAdminSchedule(Long scheduleId, Admin admin);

    // 직원 개인 일정 삭제
    void deleteEmployeeSchedule(Long scheduleId, User user);


}
