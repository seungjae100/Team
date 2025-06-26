package com.web.team.employee.dto;

import com.web.team.user.domain.User;

public record EmployeeAdminResponse(String name, String position, boolean isActive) {
    public static EmployeeAdminResponse from(User user) {
        return new EmployeeAdminResponse(
                user.getName(),
                user.getPosition().name(),
                user.isActive()
        );
    }
}
