package com.web.team.admin.controller;

import com.web.team.admin.dto.AdminLoginRequest;
import com.web.team.admin.dto.AdminLoginResponse;
import com.web.team.admin.dto.AdminRegisterRequest;
import com.web.team.admin.service.AdminService;
import com.web.team.jwt.CookieUtils;
import com.web.team.jwt.CustomAdminDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "관리자 CRUD 관려 API")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "관리자 등록에 대한 API", description = "관리자 회원가입을 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "관리자가 등록되었습니다."),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @PostMapping("/register")
    public ResponseEntity<String> adminRegister(@RequestBody AdminRegisterRequest request) {
        adminService.adminRegister(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("관리자 등록이 완료되었습니다.");
    }

    @Operation(summary = "관리자 로그인에 대한 API", description = "관리자 로그인을 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인에 성공했습니다."),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @PostMapping("/login")
    public ResponseEntity<String> adminLogin(@RequestBody AdminLoginRequest request,
                                                         HttpServletResponse response) {
        AdminLoginResponse loginResponse = adminService.adminLogin(request);

        CookieUtils.setCookie(response, "accessToken", loginResponse.accessToken(), 60 * 60 * 12);
        return ResponseEntity.ok("관리자 로그인 성공");
    }

    @Operation(summary = "관리자 로그아웃에 대한 API", description = "관리자 로그아웃을 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자가 로그아웃되었습니다."),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @PostMapping("/logout")
    public ResponseEntity<String> adminLogout(@AuthenticationPrincipal CustomAdminDetails adminDetails,
                                              HttpServletResponse response) {
        // 1. Redis 에서 RefreshToken 삭제
        adminService.adminLogout(adminDetails);

        // 2. 클라이언트 쿠키 삭제
        CookieUtils.deleteCookie(response, "accessToken");

        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }

    @Operation(summary = "관리자 토큰 재발급에 대한 API", description = "토큰 재발급을 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급이 완료되었습니다."),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
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
