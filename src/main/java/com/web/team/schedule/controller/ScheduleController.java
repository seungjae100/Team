package com.web.team.schedule.controller;

import com.web.team.jwt.CustomAdminDetails;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.schedule.dto.ScheduleCalendarResponse;
import com.web.team.schedule.dto.ScheduleCreateRequest;
import com.web.team.schedule.dto.ScheduleResponse;
import com.web.team.schedule.dto.ScheduleUpdateRequest;
import com.web.team.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/company")
    public ResponseEntity<Void> createAdminSchedule(
            @RequestBody ScheduleCreateRequest request,
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
       scheduleService.createAdminSchedule(request, adminDetails.getAdmin());
       return ResponseEntity.ok().build();
    }

    @GetMapping("/company")
    public ResponseEntity<List<ScheduleCalendarResponse>> getAdminSchedules(
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
        List<ScheduleCalendarResponse> schedules = scheduleService.getSchedulesByAdmin(adminDetails.getAdmin());
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/companyAdmin/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getScheduleForAdmin(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
        ScheduleResponse response = scheduleService.getScheduleForAdmin(scheduleId, adminDetails.getAdmin());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/employee")
    public ResponseEntity<Void> createEmployeeSchedule(
            @RequestBody ScheduleCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        scheduleService.createEmployeeSchedule(request, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/employee")
    public ResponseEntity<List<ScheduleCalendarResponse>> getEmployeeSchedules(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ScheduleCalendarResponse> schedules = scheduleService.getSchedulesByEmployee(userDetails.getUser());
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/employee/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getScheduleForEmployee(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ScheduleResponse response = scheduleService.getScheduleForEmployee(scheduleId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/employee/{scheduleId}")
    public ResponseEntity<Void> updateEmployeeSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        scheduleService.updateEmployeeSchedule(scheduleId,userDetails.getUser(), request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/companyAdmin/{scheduleId}")
    public ResponseEntity<Void> updateAdminSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleUpdateRequest request,
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
        scheduleService.updateAdminSchedule(scheduleId, adminDetails.getAdmin(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/employee/{scheduleId}")
    public ResponseEntity<Void> deleteEmployeeSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        scheduleService.deleteEmployeeSchedule(scheduleId, userDetails.getUser());
        return ResponseEntity.noContent().build(); // 204 반환
    }

    @DeleteMapping("/companyAdmin/{scheduleId}")
    public ResponseEntity<Void> deleteEmployeeSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
        scheduleService.deleteAdminSchedule(scheduleId, adminDetails.getAdmin());
        return ResponseEntity.noContent().build(); // 204 반환
    }
}
