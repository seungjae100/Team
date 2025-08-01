package com.web.team.user.controller;

import com.web.team.jwt.BasePrincipal;
import com.web.team.jwt.CookieUtils;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.dto.PasswordChangeRequest;
import com.web.team.user.dto.UserLoginRequest;
import com.web.team.user.dto.UserLoginResponse;
import com.web.team.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "accessToken")
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "직원 API", description = "직원 CRUD입니다.")
public class UserController {

    private final UserService userService;

    @Operation(summary = "직원 로그인에 대한 API", description = "직원이 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인에 성공했습니다."),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> userLogin(@RequestBody UserLoginRequest request,
                                            HttpServletResponse response) {
        UserLoginResponse loginResponse = userService.userLogin(request);
        CookieUtils.setCookie(response, "accessToken", loginResponse.accessToken(), 60 * 60 * 12);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "직원의 로그아웃에 대한 API", description = "직원의 로그아웃을 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃에 성공했습니다."),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @PostMapping("/logout")
    public ResponseEntity<String> userLogout(@AuthenticationPrincipal BasePrincipal principal,
                                             HttpServletResponse response) {
        // 1. Redis 에서 RefreshToken 삭제
        userService.userLogout(principal);

        // 2. 쿠키삭제
        CookieUtils.deleteCookie(response,"accessToken");

        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }

    @Operation(summary = "직원의 정보 수정 대한 API", description = "직원 자신의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정이 완료되었습니다."),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
    @PatchMapping("/password")
    public ResponseEntity<String> userChangePassword(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userDetails.getUser(), request);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    @Operation(summary = "직원의 토큰 재발급에 대한 API", description = "토큰의 재발급을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급에 성공했습니다."),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
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
