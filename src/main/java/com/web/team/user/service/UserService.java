package com.web.team.user.service;

import com.web.team.exception.CustomException;
import com.web.team.exception.ErrorCode;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.domain.User;
import com.web.team.user.dto.*;
import com.web.team.jwt.JwtTokenProvider;
import com.web.team.jwt.TokenService;
import com.web.team.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
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
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        tokenService.storeRefreshToken(user.getId(), refreshToken);

        return new UserLoginResponse(accessToken);
    }

    // 직원 계정 로그인 유저 검증 처리
    @Transactional(readOnly = true)
    public User userLoginCheck(UserLoginRequest request) {

        // DB에서 이메일로 사용자를 조회
        User user = userRepository.findActiveUserByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 로그인 성공
        return user;
    }

    // 직원의 비밀번호 변경
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.changePassword(passwordEncoder.encode(request.getPassword()));
    }

    // 직원의 로그아웃
    @Transactional
    public void userLogout(CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        tokenService.deletedRefreshToken(userId);
    }

    // 직원 AccessToken 재발급
    public void reAccessToken(HttpServletResponse response, String expiredAccessToken) {
        tokenService.reAccessToken(response, expiredAccessToken);
    }


}
