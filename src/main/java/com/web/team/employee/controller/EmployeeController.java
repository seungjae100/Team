package com.web.team.employee.controller;

import com.web.team.employee.service.EmployeeService;
import com.web.team.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    // 전체 직원 목록 조회 (관리자 : 전체 / 직원 : 활성화 계정만 조회가능)
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getAllEmployees(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(employeeService.getAllEmployees(userDetails));
    }

}
