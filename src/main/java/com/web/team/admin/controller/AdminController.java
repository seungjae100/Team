package com.web.team.admin.controller;

import com.web.team.admin.dto.AdminLoginRequest;
import com.web.team.admin.dto.AdminLoginResponse;
import com.web.team.admin.dto.AdminRegisterRequest;
import com.web.team.admin.service.AdminService;
import com.web.team.jwt.CustomAdminDetails;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.jwt.TokenService;
import com.web.team.user.dto.UserRegisterRequest;
import com.web.team.user.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    // 관리자 계정 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> adminRegister(@RequestBody AdminRegisterRequest request) {
        adminService.adminRegister(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("관리자 등록이 완료되었습니다.");
    }

    // 관리자 계정 로그인
    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> adminLogin(@RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminService.adminLogin(request);
        return ResponseEntity.ok(response);
    }

    // 관리자 계정 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> adminLogout(@AuthenticationPrincipal CustomAdminDetails adminDetails) {
        adminService.adminLogout(adminDetails);
        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }


    // 직원 계정 회원가입
    @PostMapping("/user/register")
    public ResponseEntity<String> userRegister(@RequestBody UserRegisterRequest request) {
        adminService.userRegister(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("직원 등록이 완료되었습니다.");
    }

    // 직원 정보 수정
    @PatchMapping("/user/update")
    public ResponseEntity<String> userUpdate(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserUpdateRequest request) {
        adminService.userUpdate(userDetails.getUserId(), request);
        return ResponseEntity.ok("정보 수정 완료");
    }

}
