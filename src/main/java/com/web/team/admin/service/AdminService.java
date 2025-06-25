package com.web.team.admin.service;

import com.web.team.admin.domain.Admin;
import com.web.team.admin.dto.AdminLoginRequest;
import com.web.team.admin.dto.AdminLoginResponse;
import com.web.team.admin.dto.AdminRegisterRequest;
import com.web.team.admin.repository.AdminRepository;
import com.web.team.jwt.CustomAdminDetails;
import com.web.team.jwt.JwtTokenProvider;
import com.web.team.jwt.TokenService;
import com.web.team.user.domain.User;
import com.web.team.user.dto.UserRegisterRequest;
import com.web.team.user.dto.UserUpdateRequest;
import com.web.team.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    // 관리자 계정 회원가입
    @Transactional
    public void adminRegister(AdminRegisterRequest request) {

        adminRepository.existsAdminByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("이미 사용중인 아이디입니다."));

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
        String accessToken = jwtTokenProvider.createAccessToken(admin.getId(), admin.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        tokenService.storeRefreshToken(admin.getId(), refreshToken);

        return new AdminLoginResponse(accessToken);
    }

    // 관리자 계정 유효성 검증
    @Transactional(readOnly = true)
    public Admin adminLoginCheck(AdminLoginRequest request) {
        // DB에서 아이디로 사용자 조회 중복확인
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("이미 사용중인 아이디입니다."));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return admin;
    }

    // 관리자 로그아웃
    @Transactional
    public void adminLogout(CustomAdminDetails adminDetails) {
        Long adminId = adminDetails.getAdminId();
        tokenService.deletedRefreshToken(adminId);
    }

    // 관리자 AccessToken 재발급
    @Transactional
    public void reAccessToken(HttpServletResponse response, String expiredAccessToken) {
        tokenService.reAccessToken(response, expiredAccessToken);
    }


    // 직원 계정 회원가입
    @Transactional
    public void userRegister(UserRegisterRequest request) {

        // 활성화/비활성화를 모두 포함하여 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        User user = User.create(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getPosition()
        );

        userRepository.save(user);
    }

    // 관리자의 직원 정보 수정
    @Transactional
    public void userUpdate(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다."));

        user.updateUser(request.getName(), request.getPosition(), request.getIsActive());
    }



}
