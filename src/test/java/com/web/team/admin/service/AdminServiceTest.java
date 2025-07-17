package com.web.team.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.web.team.admin.domain.Admin;
import com.web.team.admin.dto.AdminLoginRequest;
import com.web.team.admin.dto.AdminLoginResponse;
import com.web.team.admin.dto.AdminRegisterRequest;
import com.web.team.admin.repository.AdminRepository;
import com.web.team.exception.CustomException;
import com.web.team.exception.ErrorCode;
import com.web.team.jwt.CustomAdminDetails;
import com.web.team.jwt.JwtTokenProvider;
import com.web.team.jwt.TokenService;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletResponse response;


    @DisplayName("adminRegister 성공 - 회원가입 성공")
    @Test
    void adminRegister_success() {
        // given
        AdminRegisterRequest request = new AdminRegisterRequest("admin", "password1234", "관리자");

        when(adminRepository.existsByUsername("admin")).thenReturn(false);

        // when
        adminService.adminRegister(request);

        // then
        verify(adminRepository).save(any(Admin.class));        
        
    }

    @DisplayName("adminRegister 실패 - 중복된 아이디")
    @Test
    void adminRegister_fail_duplicateUsername() {
        // given
        AdminRegisterRequest request = new AdminRegisterRequest("admin", "password1234", "관리자");

        when(adminRepository.existsByUsername("admin")).thenReturn(true);

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> {
            adminService.adminRegister(request);
        });

        assertEquals(ErrorCode.DUPLICATE_ADMIN_USERNAME, e.getErrorCode());
    }

    @DisplayName("adminLogin 성공")
    @Test
    void adminLogin_success() {
        // given
        AdminLoginRequest request = new AdminLoginRequest("admin", "password1234");

        Admin admin = Admin.create("admin", "encodePw", "관리자");
        ReflectionTestUtils.setField(admin, "id", 1L);

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("password1234", "encodePw")).thenReturn(true);
        when(jwtTokenProvider.createAccessToken(1L, "ADMIN")).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken()).thenReturn("refresh-token");

        // when
        AdminLoginResponse response = adminService.adminLogin(request);

        // then
        assertEquals("access-token", response.accessToken());

        verify(tokenService).storeRefreshToken(1L, "refresh-token");
    }

    @DisplayName("adminLogin 실패 - 아이디 데이터베이스에 없음")
    @Test
    void adminLogin_fail_adminNotFound() {
        // given
        AdminLoginRequest request = new AdminLoginRequest("admin", "password1234");
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.empty());

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> {
            adminService.adminLogin(request);
        });

        assertEquals(ErrorCode.ADMIN_NOT_FOUND, e.getErrorCode());
        
    }

    @DisplayName("adminLogin 실패 - 비밀번호 불일치")
    @Test
    void adminLogin_fail_wrongPassword() {
        // given
        AdminLoginRequest request = new AdminLoginRequest("admin", "wrongPw");

        Admin admin = Admin.create("admin", "encodePw", "관리자");
        ReflectionTestUtils.setField(admin, "id", 1L);

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrongPw", "encodePw")).thenReturn(false);

        // when & then
        CustomException e = assertThrows(CustomException.class, () -> {
            adminService.adminLogin(request);
        });

        assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
    }

    @DisplayName("adminLogout 성공")
    @Test
    void adminLogout_success() {
        // given
        CustomAdminDetails adminDetails = mock(CustomAdminDetails.class);
        when(adminDetails.getAdminId()).thenReturn(1L);

        // when
        adminService.adminLogout(adminDetails);

        // then
        verify(tokenService).deletedRefreshToken(1L);
    }

    @DisplayName("reAccessToken 성공")
    @Test
    void reAccessToken_success() {
        // given
        String expiredToken = "expired-token";

        // when
        adminService.reAccessToken(response, expiredToken);

        // then
        verify(tokenService).reAccessToken(response, expiredToken);
    }
}
