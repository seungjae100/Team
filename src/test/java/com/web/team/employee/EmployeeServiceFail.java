package com.web.team.employee;

import com.web.team.employee.service.EmployeeServiceImpl;
import com.web.team.exception.CustomException;
import com.web.team.exception.ErrorCode;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.domain.Position;
import com.web.team.user.domain.Role;
import com.web.team.user.domain.User;
import com.web.team.user.dto.UserRegisterRequest;
import com.web.team.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceFail {

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private UserRepository userRepository;

    // 🔹 CustomUserDetails 생성
    private CustomUserDetails createUserDetails(Role role) {
        User user = new User(
                1L,
                "test@email.com",
                "encodedPw",
                "홍길동",
                role,
                Position.MANAGER,
                true,
                new ArrayList<>(),
                new ArrayList<>()
        );
        return new CustomUserDetails(user);
    }

    @Test
    @DisplayName("직원 등록 - 실패 (중복 이메일)")
    void registerEmployee_fail_existsEmail() {
        // given
        UserRegisterRequest request = new UserRegisterRequest("test@gmail.com", "password", "홍길동", Position.INTURN);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // when & then
        CustomException error = assertThrows(CustomException.class, () -> employeeService.registerEmployee(request));
        assertEquals(ErrorCode.DUPLICATE_EMAIL, error.getErrorCode());
    }
}
