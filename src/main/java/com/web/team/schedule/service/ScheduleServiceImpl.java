package com.web.team.schedule.service;

import com.web.team.admin.domain.Admin;
import com.web.team.schedule.domain.Schedule;
import com.web.team.schedule.dto.ScheduleCreateRequest;
import com.web.team.schedule.repository.ScheduleRepository;
import com.web.team.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Override
    public void createCompanySchedule(ScheduleCreateRequest request, Admin admin) {
        Schedule schedule = Schedule.create(
                request.title(), request.content(), request.startedAt(), request.endAt(), request.type(), admin
        );
        scheduleRepository.save(schedule);
    }

    @Override
    public void createEmployeeSchedule(ScheduleCreateRequest request, User user) {
        Schedule schedule = Schedule.create(
                request.title(), request.content(), request.startedAt(), request.endAt(), request.type(), user
        );
        scheduleRepository.save(schedule);
    }
}
