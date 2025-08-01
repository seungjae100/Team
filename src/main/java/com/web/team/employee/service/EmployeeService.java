package com.web.team.employee.service;

import com.web.team.jwt.BasePrincipal;
import com.web.team.user.dto.UserRegisterRequest;
import com.web.team.user.dto.UserUpdateRequest;

public interface EmployeeService {

    Object getAllEmployees(BasePrincipal principal);

    void registerEmployee(UserRegisterRequest request);

    void updateEmployee(BasePrincipal principal, UserUpdateRequest request);

    Object getEmployeeById(Long id, BasePrincipal principal);

}
