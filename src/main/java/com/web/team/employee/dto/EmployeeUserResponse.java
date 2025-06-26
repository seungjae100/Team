package com.web.team.employee.dto;

import com.web.team.user.domain.User;

public record EmployeeUserResponse(String name, String position) {
    public static EmployeeUserResponse from(User user) {
        return new EmployeeUserResponse(
                user.getName(),
                user.getPosition().name()
        );
    }
}
