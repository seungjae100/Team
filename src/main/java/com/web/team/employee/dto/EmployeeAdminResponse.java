package com.web.team.employee.dto;

import com.web.team.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자의 직원 리스트 확인 응답 DTO")
public record EmployeeAdminResponse(@Schema(description = "직원 이름", example = "홍길동")
                                    String name,
                                    @Schema(description = "직급", example = "MANAGER")
                                    String position,
                                    @Schema(description = "활성 상태", example = "true")
                                    boolean isActive) {
    public static EmployeeAdminResponse from(User user) {
        return new EmployeeAdminResponse(
                user.getName(),
                user.getPosition().name(),
                user.isActive()
        );
    }
}
