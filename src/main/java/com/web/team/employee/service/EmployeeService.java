package com.web.team.employee.service;

import com.web.team.jwt.CustomUserDetails;

public interface EmployeeService {

    Object getAllEmployees(CustomUserDetails userDetails);

}
