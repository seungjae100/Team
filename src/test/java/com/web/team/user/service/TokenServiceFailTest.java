package com.web.team.user.service;

import com.web.team.jwt.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenServiceFailTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    @DisplayName("storeRefreshToken 저장 실패 - Redis 저장 중 예외 발생")
    void storeRefreshToken_fail_dueToRedisException() {
        // given
        Long userId = 1L;
        String refreshToken = "test-refresh-token";
        String key = "refresh_token:" + userId;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Redis 저장 시 예외 발생하도록 설정
        doThrow(new RuntimeException("Redis 저장 실패"))
                .when(valueOperations).set(eq(key), eq(refreshToken), eq(Duration.ofDays(7)));

        // when & then
        assertThrows(RuntimeException.class, () -> {
            tokenService.storeRefreshToken(userId, refreshToken);
        });

    }

    @Test
    @DisplayName("getStoredRefreshToken 실패 - 저장된 RefreshToken 없음")
    void getStoredRefreshToken_fail_tokenNotFound() {
        // given
        Long userId = 1L;
        String key = "refresh_token:" + userId;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);

        // when
        String result = tokenService.getStoredRefreshToken(userId);

        // then
        assertNull(result); // 결과가 null 일 때
    }

    @Test
    @DisplayName("deletedRefreshToken 실패 - Redis 삭제 중 예외 발생")
    void deletedRefreshToken_fail_dueToRedisException() {
        // given
        Long userId = 1L;
        String key = "refresh_tokenL:" + userId;

        // Redis
        doThrow(new RuntimeException("Redis 삭제 실패"))
                .when(redisTemplate).delete(key);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            tokenService.deletedRefreshToken(userId);
        });

    }
}
