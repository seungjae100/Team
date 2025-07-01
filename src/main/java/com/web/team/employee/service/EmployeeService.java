package com.web.team.employee.service;

import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.dto.UserRegisterRequest;
import com.web.team.user.dto.UserUpdateRequest;

public interface EmployeeService {

    Object getAllEmployees(CustomUserDetails userDetails);

    void registerEmployee(UserRegisterRequest request);

    void updateEmployee(Long userId, UserUpdateRequest request);

    Object getEmployeeById(Long id, CustomUserDetails userDetails);

}
