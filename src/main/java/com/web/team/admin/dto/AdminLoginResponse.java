package com.web.team.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 로그인 응답 DTO")
public record AdminLoginResponse(

        @Schema(description = "토큰을 반환", example = "feswfeaw2j0fe-.....")
        String accessToken
) {

}
