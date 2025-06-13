package com.web.team.user.service;

import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.domain.User;
import com.web.team.user.dto.*;
import com.web.team.jwt.JwtTokenProvider;
import com.web.team.jwt.TokenService;
import com.web.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    // 직원 계정 로그인 비즈니스 처리
    @Transactional
    public UserLoginResponse userLogin(UserLoginRequest request) {

        User user = userLoginCheck(request);

        // 토큰 발급(AccessToken) + RefreshToken 저장
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        tokenService.storeRefreshToken(user.getId(), refreshToken);

        return new UserLoginResponse(accessToken);
    }

    // 직원 계정 로그인 유저 검증 처리
    @Transactional(readOnly = true)
    public User userLoginCheck(UserLoginRequest request) {

        // DB에서 이메일로 사용자를 조회
        User user = userRepository.findActiveUserByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않거나 비활성화된 계정입니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공
        return user;
    }

    // 직원의 비밀번호 변경
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        user.changePassword(passwordEncoder.encode(request.getPassword()));
    }

    // 직원의 로그아웃
    @Transactional
    public void userLogout(CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        tokenService.deletedRefreshToken(userId);
    }

    // 직원의 reAccessToken 발급
    @Transactional
    public String reAccessToken(String expiredAccessToken) {
        // 1. AccessToken이 만료된건지 확인
        if (!jwtTokenProvider.isExpired(expiredAccessToken)) {
            throw new RuntimeException("AccessToken이 아직 만료되지 않았습니다.");
        }

        // 2. 만료된 토큰에서 userId 추출
        Long userId = jwtTokenProvider.getUserId(expiredAccessToken);

        // 3. Redis 에서 userId 기반으로 저장된 RefreshToken 조회
        String storedRefreshToken = tokenService.getStoredRefreshToken(userId);
        if (storedRefreshToken == null) {
            throw new RuntimeException("RefreshToken이 존재하지 않습니다. 다시 로그인하세요");
        }

        // 4. 새 AccessToken 발급 -> 클라이언트에 반환
        String role = jwtTokenProvider.getRole(expiredAccessToken);
        return jwtTokenProvider.createAccessToken(userId, role);
    }
}
