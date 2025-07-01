package com.web.team.employee;

import com.web.team.employee.dto.EmployeeAdminResponse;
import com.web.team.employee.dto.EmployeeUserResponse;
import com.web.team.employee.service.EmployeeServiceImpl;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // CustomUserDetails
    private CustomUserDetails createUserDetails(Role role) {
        User user = new User(
                1L,
                "test@gmail.com",
                "encodedPw",
                "홍길동",
                role,
                Position.MANAGER,
                true,
                new ArrayList<>(),
                new ArrayList<>()
        ) ;
        return new CustomUserDetails(user);
    }

    // 회원가입 요청 DTO
    private UserRegisterRequest createRegisterRequest(String email) {
        return new UserRegisterRequest(email, "password", "홍길동", Position.MANAGER);
    }

    @Test
    @DisplayName("직원 등록 - 성공")
    void registerEmployee_success() {
        // given
        UserRegisterRequest request = createRegisterRequest("test@gmail.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("암호화된비밀번호");

        // when
        employeeService.registerEmployee(request);

        // then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("직원 목록 조회 - 관리자")
    void getAllEmployees_admin() {
        // given
        CustomUserDetails adminDetails = createUserDetails(Role.ADMIN);
        List<User> mockUsers = List.of(
                new User(1L, "abc@abc.com", "password", "홍길동", Role.USER, Position.INTURN, true, new ArrayList<>(), new ArrayList<>()),
                new User(2L, "dev@abc.com", "password1", "도우너", Role.USER, Position.MANAGER, true, new ArrayList<>(), new ArrayList<>())
        );

        when(userRepository.findAll()).thenReturn(mockUsers);

        // when
        Object result = employeeService.getAllEmployees(adminDetails);

        // then
        verify(userRepository, times(1)).findAll();
        assertTrue(result instanceof List<?>);
        assertTrue(((List<?>) result).get(0) instanceof EmployeeAdminResponse);

    }

    @Test
    @DisplayName("직원 목록 조회 - 직원")
    void getAllEmployees_user() {
        // given
        CustomUserDetails userDetails = createUserDetails(Role.USER);
        List<User> activeUsers = List.of(
                new User(3L, "c@abc.com", "password2", "둘리", Role.USER, Position.ASSISTANT_MANAGER, true, new ArrayList<>(), new ArrayList<>())

        );
        when(userRepository.findByIsActiveTrue()).thenReturn(activeUsers);

        // when
        Object result = employeeService.getAllEmployees(userDetails);

        // then
        verify(userRepository, times(1)).findByIsActiveTrue();
        assertTrue(result instanceof List<?>);
        assertTrue(((List<?>) result).get(0) instanceof EmployeeUserResponse);

    }

    @Test
    @DisplayName("직원 조회 - 관리자 (전체 정보 반환)")
    void getEmployeeById_admin_success() {
        // given
        CustomUserDetails admin = createUserDetails(Role.ADMIN);
        User itsUser = new User(
                10L, "itsUser@gmail.com", "password", "김말광",
                Role.USER, Position.SENIOR_MANAGER, true,
                new ArrayList<>(), new ArrayList<>()
        );

        when(userRepository.findById(10L)).thenReturn(java.util.Optional.of(itsUser));

        // when
        Object result = employeeService.getEmployeeById(10L, admin);

        // then
        assertInstanceOf(EmployeeAdminResponse.class, result);
    }

    @Test
    @DisplayName("직원 조회 - 일반 직원 (최소 정보 반환)")
    void getEmployeeById_user_success() {
        // given
        CustomUserDetails user = createUserDetails(Role.USER);
        User itsUser = new User(
                11L, "isUser@gmail.com", "password", "홍길동",
                Role.USER, Position.MANAGER, true,
                new ArrayList<>(), new ArrayList<>()
        );

        when(userRepository.findById(11L)).thenReturn(java.util.Optional.of(itsUser));

        // when
        Object result = employeeService.getEmployeeById(11L, user);

        // then
        assertInstanceOf(EmployeeUserResponse.class, result);
    }




}
