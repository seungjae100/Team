package com.web.team.employee.service;

import com.web.team.employee.dto.EmployeeAdminResponse;
import com.web.team.employee.dto.EmployeeUserResponse;
import com.web.team.jwt.CustomAdminDetails;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.domain.Role;
import com.web.team.user.domain.User;
import com.web.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository userRepository;

    @Override
    public Object getAllEmployees(CustomUserDetails userDetails) {
        Role role = userDetails.getRole();

        List<User> users = (role == Role.ADMIN)
                ? userRepository.findAll()
                : userRepository.findByIsActiveTrue();

        return (role == Role.ADMIN)
                ? users.stream().map(EmployeeAdminResponse::from).toList()
                : users.stream().map(EmployeeUserResponse::from).toList();
    }


}
