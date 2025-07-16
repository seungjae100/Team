package com.web.team.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저의 로그인 응답 DTO")
public record UserLoginResponse(

        @Schema(description = "토큰값으로 응답을 준다.", example = "wsfjwajifjiew231-fjekalw..")
        String accessToken
) {

}
