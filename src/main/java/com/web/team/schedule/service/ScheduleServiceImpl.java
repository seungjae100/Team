package com.web.team.schedule.service;

import com.web.team.admin.domain.Admin;
import com.web.team.exception.CustomException;
import com.web.team.exception.ErrorCode;
import com.web.team.schedule.domain.Schedule;
import com.web.team.schedule.dto.ScheduleCalendarResponse;
import com.web.team.schedule.dto.ScheduleCreateRequest;
import com.web.team.schedule.dto.ScheduleResponse;
import com.web.team.schedule.dto.ScheduleUpdateRequest;
import com.web.team.schedule.repository.ScheduleRepository;
import com.web.team.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    // 관리자가 회사 일정을 등록
    @Override
    public void createAdminSchedule(ScheduleCreateRequest request, Admin admin) {
        Schedule schedule = Schedule.create(
                request.title(), request.content(), request.startedAt(), request.endAt(), request.type(), admin
        );
        scheduleRepository.save(schedule);
    }

    // 직원이 개인 일정을 등록
    @Override
    public void createEmployeeSchedule(ScheduleCreateRequest request, User user) {
        Schedule schedule = Schedule.create(
                request.title(), request.content(), request.startedAt(), request.endAt(), request.type(), user
        );
        scheduleRepository.save(schedule);
    }

    @Override
    public List<ScheduleCalendarResponse> getSchedulesByEmployee(User user) {
        return scheduleRepository.findAllForEmployee(user).stream()
                .map(ScheduleCalendarResponse::from)
                .toList();
    }

    @Override
    public List<ScheduleCalendarResponse> getSchedulesByAdmin(Admin admin) {
        return scheduleRepository.findAllForAdmin(admin).stream()
                .map(ScheduleCalendarResponse::from)
                .toList();
    }

    @Override
    public ScheduleResponse getScheduleForEmployee(Long scheduleId, User user) {
        Schedule schedule = scheduleRepository.findByIdForEmployee(scheduleId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        return ScheduleResponse.from(schedule);
    }

    @Override
    public ScheduleResponse getScheduleForAdmin(Long scheduleId, Admin admin) {
        Schedule schedule = scheduleRepository.findByIdForAdmin(scheduleId, admin)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        return ScheduleResponse.from(schedule);
    }

    @Override
    public void updateEmployeeSchedule(Long scheduleId, User user, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!user.equals(schedule.getUser())) {
            throw new AccessDeniedException("본인이 등록한 일정만 수정할 수 있습니다.");
        }

        schedule.update(
                request.title(),
                request.content(),
                request.startedAt(),
                request.endAt(),
                request.type()
        );
    }

    @Override
    public void updateAdminSchedule(Long scheduleId, Admin admin, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!admin.equals(schedule.getAdmin())) {
            throw new AccessDeniedException("본인이 등록한 회사 일정만 수정할 수 있습니다.");
        }

        schedule.update(
                request.title(),
                request.content(),
                request.startedAt(),
                request.endAt(),
                request.type()
        );
    }

    @Override
    public void deleteAdminSchedule(Long scheduleId, Admin admin) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!schedule.getAdmin().equals(admin)) {
            throw new CustomException(ErrorCode.SCHEDULE_FORBIDDEN);
        }

        scheduleRepository.delete(schedule);
    }

    @Override
    public void deleteEmployeeSchedule(Long scheduleId, User user) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!schedule.getUser().equals(user)) {
            throw new CustomException(ErrorCode.SCHEDULE_FORBIDDEN);
        }

        scheduleRepository.delete(schedule);
    }
}
