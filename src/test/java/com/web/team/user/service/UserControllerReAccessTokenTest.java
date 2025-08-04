package com.web.team.user.service;

import com.web.team.user.controller.UserController;
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
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerReAccessTokenTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;


    @Test
    @DisplayName("AccessToken 재발급 성공")
    void reAccessToken_success() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Cookie expiredToken = new Cookie("accessToken", "expired-token");
        Cookie[] cookies = new Cookie[] {expiredToken};

        when(request.getCookies()).thenReturn(cookies);

        // when
        ResponseEntity<String> result = userController.reAccessToken(request, response);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("AccessToken이 재발급 되었습니다.", result.getBody());
        verify(userService).reAccessToken(response, "expired-token");

    }

    @Test
    @DisplayName("AccessToken 재발급 실패 - 쿠키가 없음")
    void reAccessToken_fail_onCookie() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(null); // 쿠키가 없어

        // when & then
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> userController.reAccessToken(request, response)
        );

        assertEquals("AccessToken이 존재하지 않습니다.", exception.getMessage());
    }
}
