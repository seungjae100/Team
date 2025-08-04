package com.web.team.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "비밀번호 변경 요청 DTO")
public class PasswordChangeRequest {

    @Schema(description = "직원의 비밀번호", example = "password1234")
    private String password;
}
