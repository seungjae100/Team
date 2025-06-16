package com.web.team.user.service;

import com.web.team.jwt.JwtTokenProvider;
import com.web.team.jwt.TokenService;
import com.web.team.user.domain.Position;
import com.web.team.user.domain.User;
import com.web.team.user.dto.PasswordChangeRequest;
import com.web.team.user.dto.UserLoginRequest;
import com.web.team.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceFailTest {

    @InjectMocks
    private UserService userService;

    @Mock // 테스트 대상 클래스가 의존하는 객체(UserRepository)를 가짜로 생성
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenService tokenService;

    @Test
    @DisplayName("userLoginCheck() 실패 - - 비밀번호 틀림")
    void userLoginFail_password() {

        // given
        UserLoginRequest request = new UserLoginRequest("user1@gmail.com", "wrongPassword");
        User user = User.create("user1@gmail.com", "securityPassword", "김지은", Position.INTURN);

        when(userRepository.findActiveUserByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .thenReturn(false);

        // when
        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            userService.userLoginCheck(request);
        });

        // then
        assertEquals("비밀번호가 일치하지 않습니다.", e.getMessage());

    }

    @Test
    @DisplayName("userLoginCheck() 실패 - 존재하거나 비활성화된 계정")
    void userLoginCheck_fail_userNotFound() {
        // given
        UserLoginRequest request = new UserLoginRequest("user1@gmail.com", "password");

        when(userRepository.findActiveUserByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        // when
        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            userService.userLoginCheck(request);
        });

        // then
        assertEquals("존재하지 않거나 비활성화된 계정입니다.", e.getMessage());
    }

    @Test
    @DisplayName("userLogin() 실패 - AccessToken 생성 실패")
    void userLogin_fail_accessTokenCreate() {
        // given
        UserLoginRequest request = new UserLoginRequest("user1@gmail.com", "password");

        User user = User.create("user1@gmail.com", "securityPassword", "김지은", Position.INTURN);
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findActiveUserByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .thenReturn(true);

        when(jwtTokenProvider.createAccessToken(anyLong(), anyString()))
                .thenThrow(new RuntimeException("AccessToken 생성 실패"));

        // when & then
        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            userService.userLogin(request);
        });

        assertEquals("AccessToken 생성 실패", e.getMessage());
    }

    @Test
    @DisplayName("userLogin() 실패 - RefreshToken 생성 실패 (Redis)")
    void userLogin_fail_refreshTokenCreate() {

        UserLoginRequest request = new UserLoginRequest("user1@gmail.com", "password");
        User user = User.create("uesr1@gmail.com", "password", "김지은", Position.INTURN);

        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findActiveUserByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .thenReturn(true);

        when(jwtTokenProvider.createAccessToken(anyLong(), anyString()))
                .thenReturn("access-token");

        when(jwtTokenProvider.createRefreshToken())
                .thenReturn("refresh-token");

        doThrow(new RuntimeException("Redis 저장 실패"))
                .when(tokenService).storeRefreshToken(anyLong(), eq("refresh-token"));


        // when & then
        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            userService.userLogin(request);
        });

        assertEquals("Redis 저장 실패", e.getMessage());
    }

    @Test
    @DisplayName("changePassword() 실패 - 유저가 없는 상태")
    void changePassword_fail_notUser() {
        // given
        Long userId = 999L;
        PasswordChangeRequest request = new PasswordChangeRequest("password");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> userService.changePassword(userId, request));

    }



}
