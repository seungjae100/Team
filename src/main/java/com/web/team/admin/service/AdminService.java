package com.web.team.admin.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    // 관리자 계정 회원가입
    @Transactional
    public void adminRegister(AdminRegisterRequest request) {

        boolean exists = adminRepository.existsByUsername(request.getUsername());

        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_ADMIN_USERNAME);
        }


        Admin admin = Admin.create(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getName()
        );

        adminRepository.save(admin);
    }

    // 관리자 계정 로그인 (토큰 발행)
    @Transactional
    public AdminLoginResponse adminLogin(AdminLoginRequest request) {
        Admin admin = adminLoginCheck(request);

        // 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(admin.getUsername(), admin.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        tokenService.storeRefreshToken(admin.getUsername(), refreshToken);

        return new AdminLoginResponse(accessToken);
    }

    // 관리자 계정 유효성 검증
    @Transactional(readOnly = true)
    public Admin adminLoginCheck(AdminLoginRequest request) {
        // DB에서 아이디로 사용자 조회 중복확인
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return admin;
    }

    // 관리자 로그아웃
    @Transactional
    public void adminLogout(CustomAdminDetails adminDetails) {
        String loginId = adminDetails.getUsername();
        tokenService.deletedRefreshToken(loginId);
    }

    // 관리자 AccessToken 재발급
    @Transactional
    public void reAccessToken(HttpServletResponse response, String expiredAccessToken) {
        tokenService.reAccessToken(response, expiredAccessToken);
    }

}
