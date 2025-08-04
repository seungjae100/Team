package com.web.team.user.dto;

import com.web.team.user.domain.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "직원의 정보 수정에 대한 요청 DTO")
public class UserUpdateRequest {

    @Schema(description = "직원의 이름", example = "흰둥이")
    private String name;

    @Schema(description = "직원의 직급", example = "DIRECTOR")
    private Position position;

    @Schema(description = "직원의 활성화 여부", example = "true")
    private Boolean isActive;

}
