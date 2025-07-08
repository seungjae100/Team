package com.web.team.schedule.service;

import com.web.team.admin.domain.Admin;
import com.web.team.schedule.dto.ScheduleCreateRequest;
import com.web.team.user.domain.User;

public interface ScheduleService {

    void createEmployeeSchedule(ScheduleCreateRequest request, User user);

    void createCompanySchedule(ScheduleCreateRequest request, Admin admin);


}
