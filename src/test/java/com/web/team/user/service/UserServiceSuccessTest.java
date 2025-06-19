package com.web.team.user.service;

import com.web.team.jwt.CustomUserDetails;
import com.web.team.jwt.JwtTokenProvider;
import com.web.team.jwt.TokenService;
import com.web.team.user.controller.UserController;
import com.web.team.user.domain.Position;
import com.web.team.user.domain.User;
import com.web.team.user.dto.PasswordChangeRequest;
import com.web.team.user.dto.UserLoginRequest;
import com.web.team.user.dto.UserLoginResponse;
import com.web.team.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // JUnit5 + Mockito 연결
class UserServiceSuccessTest {

    @InjectMocks // 테스트 대상 객체 (UserService) 자동 생성
    private UserService userService;

    @Mock // 테스트 대상 클래스가 의존하는 객체(UserRepository)를 가짜로 생성
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserController userController;

    @Test
    @DisplayName("로그인 성공 테스트")
    void userLogin_success() {
        // given
        UserLoginRequest request = new UserLoginRequest("user1@gmail.com", "1234");

        User user = User.create("user1@gmail.com", "securityPassword", "김지은", Position.INTURN);
        ReflectionTestUtils.setField(user, "id", 1L);


        when(userRepository.findActiveUserByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .thenReturn(true);

        when(jwtTokenProvider.createAccessToken(anyLong(), eq("USER")))
                .thenReturn("access-Token");

        when(jwtTokenProvider.createRefreshToken())
                .thenReturn("refresh-Token");

        // when
        UserLoginResponse response = userService.userLogin(request);

        // then
        assertEquals("access-Token", response.getAccessToken());
        verify(tokenService).storeRefreshToken(anyLong(), eq("refresh-Token"));
    }

    @Test
    @DisplayName("changePassword() 성공 - 비밀번호 변경 완료")
    void changePassword_success() {
        // given
        Long userId = 1L;
        PasswordChangeRequest request = new PasswordChangeRequest("newPassword");

        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("암호화된 패스워드");

        // when
        userService.changePassword(userId, request);

        // then
        verify(user).changePassword("암호화된 패스워드");
    }

    @Test
    @DisplayName("로그아웃 성공")
    void userLogout_success() {
        // given
        Long userId = 1L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(userId);

        // when
        userService.userLogout(userDetails);

        // then
        verify(tokenService).deletedRefreshToken(userId);
    }





}