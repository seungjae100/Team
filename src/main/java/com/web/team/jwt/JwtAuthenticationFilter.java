package com.web.team.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.web.team.admin.domain.Admin;
import com.web.team.admin.repository.AdminRepository;
import com.web.team.exception.CustomException;
import com.web.team.exception.ErrorCode;
import com.web.team.user.domain.User;
import com.web.team.user.repository.UserRepository;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Claims claims = jwtTokenProvider.getClaims(token);
            String role = claims.get("role", String.class);
            String loginId = claims.getSubject();
            
            if("ROLE_ADMIN".equals(role)) {
                Admin admin = adminRepository.findByUsername(loginId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));
                    CustomAdminDetails adminDetails = new CustomAdminDetails(admin);
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(adminDetails, null, adminDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
            } else if ("ROLE_USER".equals(role)) {
                User user = userRepository.findByEmail(loginId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                    CustomUserDetails userDetails = new CustomUserDetails(user);
                    UsernamePasswordAuthenticationToken auth = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);                    
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        // Swagger나 Postman 등에서 사용하는 Authorization 헤더 우선
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // "Bearer " 제거
        }

        // 실제 서비스에서는 accessToken 쿠키를 사용
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
