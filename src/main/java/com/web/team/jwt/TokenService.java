package com.web.team.jwt;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private final JwtTokenProvider jwtTokenProvider;

    // RefreshToken 생성 유효기간 7일
    public void storeRefreshToken(Long userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofDays(7));
    }

    // RefreshToken 조회
    public String getStoredRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    // RefreshToken 삭제
    public void deletedRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
    }

    // 직원의 reAccessToken 발급
    @Transactional
    public void reAccessToken(HttpServletResponse response, String expiredAccessToken) {
        // 1. AccessToken이 만료된건지 확인
        if (!jwtTokenProvider.isExpired(expiredAccessToken)) {
            throw new RuntimeException("AccessToken이 아직 만료되지 않았습니다.");
        }

        // 2. 만료된 토큰에서 userId 추출
        Long userId = jwtTokenProvider.getUserId(expiredAccessToken);

        // 3. Redis 에서 userId 기반으로 저장된 RefreshToken 조회
        String storedRefreshToken = getStoredRefreshToken(userId);
        if (storedRefreshToken == null) {
            throw new RuntimeException("RefreshToken이 존재하지 않습니다. 다시 로그인하세요");
        }

        // 4. 새 AccessToken 발급 및 쿠키로 전환
        String role = jwtTokenProvider.getRole(expiredAccessToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(userId, role);

        // 쿠키로 응답에 심기
        CookieUtils.setCookie(response, "accessToken", newAccessToken, 60 * 30);

    }
}
