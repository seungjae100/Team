package com.web.team.employee.service;

import com.web.team.employee.dto.EmployeeAdminResponse;
import com.web.team.employee.dto.EmployeeUserResponse;
import com.web.team.exception.CustomException;
import com.web.team.exception.ErrorCode;
import com.web.team.jwt.BasePrincipal;
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
    public void updateEmployee(BasePrincipal principal, UserUpdateRequest request) {
        User user = userRepository.findByEmail(principal.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (request.getIsActive() != null && principal.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        user.updateUser(request.getName(), request.getPosition(), request.getIsActive());
    }

    @Transactional(readOnly = true)
    @Override
    public Object getAllEmployees(BasePrincipal principal) {
    
        List<User> users = (principal.getRole() == Role.ADMIN)
                ? userRepository.findAll()
                : userRepository.findByIsActiveTrue();

        return (principal.getRole() == Role.ADMIN)
                ? users.stream().map(EmployeeAdminResponse::from).toList()
                : users.stream().map(EmployeeUserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Object getEmployeeById(Long userId, BasePrincipal principal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (principal.getRole() == Role.ADMIN) {
            return EmployeeAdminResponse.from(user);
        }

        return EmployeeUserResponse.from(user);
    }
}
