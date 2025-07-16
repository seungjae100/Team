package com.web.team.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "관리자 로그인 요청 DTO")
public class AdminLoginRequest {

    @Schema(description = "관리자 아이디를 입력합니다.", example = "관리자 1")
    private String username;

    @Schema(description = "관리자 비밀번호를 입력합니다.", example = "Password123")
    private String password;
}
