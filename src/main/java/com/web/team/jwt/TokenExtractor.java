package com.web.team.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {

    // 헤더에서 Bearer AccessToken만 추출
    public String extractAccessTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AccessDeniedException("Authorization 헤더가 존재하지 않습니다.");
        }
        return header.substring(7); // "Bearer " 제거
    }
}
