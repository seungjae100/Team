package com.web.team.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import com.web.team.user.domain.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "유저의 회원가입에 대한 요청 DTO")
public class UserRegisterRequest {

    @Schema(description = "직원의 이메일", example = "employee1@gmail.com")
    private String email;

    @Schema(description = "직원의 비밀번호", example = "password1234")
    private String password;

    @Schema(description = "직원의 이름", example = "흰둥이")
    private String name;

    @Schema(description = "직원의 직급", example = "DIRECTOR")
    private Position position;
}
