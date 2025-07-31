package com.web.team.schedule.controller;

import com.web.team.jwt.CustomAdminDetails;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.schedule.dto.ScheduleCalendarResponse;
import com.web.team.schedule.dto.ScheduleCreateRequest;
import com.web.team.schedule.dto.ScheduleResponse;
import com.web.team.schedule.dto.ScheduleUpdateRequest;
import com.web.team.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "accessToken")
@RequiredArgsConstructor
@Tag(name = "일정 API", description = "일정 (Schedule) 관련 API 입니다.")
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    //---
    @Operation(summary = "관리자 회사 일정 등록", description = "관리자가 회사 전체 일정을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정 생성 성공"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @PostMapping("/company")
    public ResponseEntity<Void> createAdminSchedule(
            @Valid @RequestBody ScheduleCreateRequest request,
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
        scheduleService.createAdminSchedule(request, adminDetails.getAdmin());
        return ResponseEntity.ok().build();
    }
    //---
    @Operation(summary = "관리자 회사 일정 전체 조회", description = "관리자가 회사 일정 전체를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회사 전체 일정 조회 성공"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @GetMapping("/company")
    public ResponseEntity<List<ScheduleCalendarResponse>> getAdminSchedules(
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
        List<ScheduleCalendarResponse> schedules = scheduleService.getSchedulesByAdmin(adminDetails.getAdmin());
        return ResponseEntity.ok(schedules);
    }
    //---
    @Operation(summary = "관리자 회사 일정 상세 조회", description = "관리자가 회사 일정을 상세조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회사 일정 상세 조회 성공"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @GetMapping("/company/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getScheduleForAdmin(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
        ScheduleResponse response = scheduleService.getScheduleForAdmin(scheduleId, adminDetails.getAdmin());
        return ResponseEntity.ok(response);
    }
    //---
    @Operation(summary = "직원 일정 등록", description = "직원이 개인 일정을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정 생성 성공"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @PostMapping("/employee")
    public ResponseEntity<Void> createEmployeeSchedule(
            @Valid @RequestBody ScheduleCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        scheduleService.createEmployeeSchedule(request, userDetails.getUser());
        return ResponseEntity.ok().build();
    }
    //---
    @Operation(summary = "직원 회사 일정 전체 조회", description = "직원 회사 일정 전체를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "직원 일정 전체 조회 성공"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @GetMapping("/employee")
    public ResponseEntity<List<ScheduleCalendarResponse>> getEmployeeSchedules(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ScheduleCalendarResponse> schedules = scheduleService.getSchedulesByEmployee(userDetails.getUser());
        return ResponseEntity.ok(schedules);
    }
    //---
    @Operation(summary = "직원 개인 일정 상세 조회", description = "직원의 개인일정을 상세조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "직원 개인일정 상세 조회 성공"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @GetMapping("/employee/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getScheduleForEmployee(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ScheduleResponse response = scheduleService.getScheduleForEmployee(scheduleId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }
    //---
    @Operation(summary = "직원 개인 일정 수정", description = "직원의 개인일정을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "직원 개인일정 수정"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @PatchMapping("/employee/{scheduleId}")
    public ResponseEntity<Void> updateEmployeeSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        scheduleService.updateEmployeeSchedule(scheduleId,userDetails.getUser(), request);
        return ResponseEntity.ok().build();
    }
    //---
    @Operation(summary = "회사 일정 수정", description = "회사의 일정을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회사 일정 수정"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @PatchMapping("/company/{scheduleId}")
    public ResponseEntity<Void> updateAdminSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleUpdateRequest request,
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
        scheduleService.updateAdminSchedule(scheduleId, adminDetails.getAdmin(), request);
        return ResponseEntity.ok().build();
    }
    //---
    @Operation(summary = "직원 일정 삭제", description = "직원이 자신의 일정을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "개인일정 삭제 성공"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @DeleteMapping("/employee/{scheduleId}")
    public ResponseEntity<Void> deleteEmployeeSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        scheduleService.deleteEmployeeSchedule(scheduleId, userDetails.getUser());
        return ResponseEntity.noContent().build(); // 204 반환
    }
    //---
    @Operation(summary = "회사 일정 삭제", description = "관리자가 회사의 일정을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회사 일정 삭제 성공"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @DeleteMapping("/company/{scheduleId}")
    public ResponseEntity<Void> deleteEmployeeSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
        scheduleService.deleteAdminSchedule(scheduleId, adminDetails.getAdmin());
        return ResponseEntity.noContent().build(); // 204 반환
    }
}
