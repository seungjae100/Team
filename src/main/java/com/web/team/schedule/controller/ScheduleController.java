package com.web.team.schedule.controller;

import com.web.team.jwt.CustomAdminDetails;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.schedule.dto.ScheduleCreateRequest;
import com.web.team.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/company")
    public ResponseEntity<Void> createByAdmin(
            @RequestBody ScheduleCreateRequest request,
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
       scheduleService.createCompanySchedule(request, adminDetails.getAdmin());
       return ResponseEntity.ok().build();
    }

    @PostMapping("/employee")
    public ResponseEntity<Void> createByEmployee(
            @RequestBody ScheduleCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        scheduleService.createEmployeeSchedule(request, userDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
