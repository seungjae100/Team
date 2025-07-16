package com.web.team.employee.dto;

import com.web.team.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "직원의 내 정보 조회 응답 DTO")
public record EmployeeUserResponse(@Schema(description = "직원 이름", example = "홍길동")
                                   String name,

                                   @Schema(description = "직급", example = "MANAGER")
                                   String position) {
    public static EmployeeUserResponse from(User user) {
        return new EmployeeUserResponse(
                user.getName(),
                user.getPosition().name()
        );
    }
}
