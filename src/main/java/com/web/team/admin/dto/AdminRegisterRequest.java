package com.web.team.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "관리자 등록에 대한 요청 DTO")
public class AdminRegisterRequest {

    @Schema(description = "관리자가 사용할 아이디", example = "관라지 1")
    private String username;

    @Schema(description = "관리자 비밀번호", example = "password1")
    private String password;

    @Schema(description = "관리자 이름", example = "신짱구")
    private String name;

}
