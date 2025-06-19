package com.web.team.user.controller;

import com.web.team.jwt.CookieUtils;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.dto.PasswordChangeRequest;
import com.web.team.user.dto.UserLoginRequest;
import com.web.team.user.dto.UserLoginResponse;
import com.web.team.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // 직원 계정 로그인
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> userLogin(@RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.userLogin(request);
        return ResponseEntity.ok(response);
    }

    // 직원 계정 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> userLogout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             HttpServletResponse response) {
        // 1. Redis 에서 RefreshToken 삭제
        userService.userLogout(userDetails);

        // 2. 쿠키삭제
        CookieUtils.deleteCookie(response,"accessToken");

        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }

    // 직원의 비밀번호 수정
    @PatchMapping("/password")
    public ResponseEntity<String> userChangePassword(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userDetails.getUserId(), request);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    // 직원 계정 AccessToken 재발급
    @PostMapping("/reAccessToken")
    public ResponseEntity<String> reAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // 1. 요청 헤더에서 만료된 AccessToken 추출
        String expiredAccessToken = CookieUtils.getCookie(request, "accessToken")
                .orElseThrow(() -> new AccessDeniedException("AccessToken이 존재하지 않습니다."));


        // 2. UserService 에서 재발급 처리 요청 -> 새 AccessToken 반환
        userService.reAccessToken(response, expiredAccessToken);

        // 3. 응답 바디에 새 토큰을 담아서 클라이언트에 전달
        return ResponseEntity.ok("AccessToken이 재발급 되었습니다.");
    }

}
