package com.web.team.admin.controller;

import com.web.team.admin.dto.AdminLoginRequest;
import com.web.team.admin.dto.AdminLoginResponse;
import com.web.team.admin.dto.AdminRegisterRequest;
import com.web.team.admin.service.AdminService;
import com.web.team.jwt.CookieUtils;
import com.web.team.jwt.CustomAdminDetails;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.dto.UserRegisterRequest;
import com.web.team.user.dto.UserUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    public ResponseEntity<String> adminLogin(@RequestBody AdminLoginRequest request,
                                                         HttpServletResponse response) {
        AdminLoginResponse loginResponse = adminService.adminLogin(request);

        CookieUtils.setCookie(response, "accessToken", loginResponse.getAccessToken(), 60 * 60 * 12);
        return ResponseEntity.ok("관리자 로그인 성공");
    }

    // 관리자 계정 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> adminLogout(@AuthenticationPrincipal CustomAdminDetails adminDetails,
                                              HttpServletResponse response) {
        // 1. Redis 에서 RefreshToken 삭제
        adminService.adminLogout(adminDetails);

        // 2. 클라이언트 쿠키 삭제
        CookieUtils.deleteCookie(response, "accessToken");

        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }

    // 관리자 계정 AccessToken 토큰 재발급
    @PostMapping("/reAccessToken")
    public ResponseEntity<String> reAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // 1. 요청 헤더에서 만료된 AccessToken을 추출
        String expiredAccessToken = CookieUtils.getCookie(request, "accessToken")
                .orElseThrow(() -> new AccessDeniedException("AccessToken이 존재하지 않습니다."));

        // 2. AdminService 에서 재발급 처리 요청
        adminService.reAccessToken(response, expiredAccessToken);

        // 3. AccessToken 재발급
        return ResponseEntity.ok("AccessToken이 재발급 되었습니다.");
    }




}
