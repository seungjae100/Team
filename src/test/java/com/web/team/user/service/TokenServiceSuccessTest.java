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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenServiceSuccessTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    @DisplayName("storedRefreshToken 성공 - Redis에 저장")
    void storeRefreshToken_success() {
        // given
        Long userId = 1L;
        String refreshToken = "test-refresh-token";
        String expectedKey = "refresh_token:" + userId;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        tokenService.storeRefreshToken(userId, refreshToken);

        // then
        verify(valueOperations).set(eq(expectedKey), eq(refreshToken), eq(Duration.ofDays(7)));

    }

    @Test
    @DisplayName("getStoredRefreshToken 성공 - Redis에서 조회")
    void getStoredRefreshToken_success() {
        // given
        Long userId = 1L;
        String expectedKey = "refresh_token:" + userId;
        String storedKey = "test-refresh-token";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(expectedKey)).thenReturn(storedKey);

        // when
        String result = tokenService.getStoredRefreshToken(userId);

        // then
        assertEquals(storedKey, result);
    }

    @Test
    @DisplayName("deletedRefreshToken 성공 - Redis에서 삭제")
    void deletedRefreshToken_success() {
        // given
        Long userId = 1L;
        String expectedKey = "refresh_token:" + userId; // Redis 에서 삭제할 키 값

        // when
        tokenService.deletedRefreshToken(userId);

        // then
        verify(redisTemplate).delete(expectedKey); // 해당 키로 삭제를 요청한건지 확인
    }


}
