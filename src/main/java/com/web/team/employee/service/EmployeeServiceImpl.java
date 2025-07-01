package com.web.team.employee.service;

import com.web.team.employee.dto.EmployeeAdminResponse;
import com.web.team.employee.dto.EmployeeUserResponse;
import com.web.team.exception.CustomException;
import com.web.team.exception.ErrorCode;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.domain.Role;
import com.web.team.user.domain.User;
import com.web.team.user.dto.UserRegisterRequest;
import com.web.team.user.dto.UserUpdateRequest;
import com.web.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
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

    @Transactional
    @Override
    public void registerEmployee(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.create(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getPosition()
        );
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateEmployee(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (request.getIsActive() != null && !user.getRole().equals(Role.ADMIN)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        user.updateUser(request.getName(), request.getPosition(), request.getIsActive());
    }

    @Transactional(readOnly = true)
    @Override
    public Object getEmployeeById(Long userId, CustomUserDetails userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (userDetails.getRole() == Role.ADMIN) {
            return EmployeeAdminResponse.from(user);
        }

        return EmployeeUserResponse.from(user);
    }
}
