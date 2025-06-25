package com.web.team.config;

import com.web.team.jwt.JwtAuthenticationFilter;
import com.web.team.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // HTML, JS 정적 파일 허용
                        .requestMatchers(
                                "/", "/index.html","/favicon.ico",
                                "/admin","/admin/**", "/js/admin/**","/pages/admin/**",
                                "/user","/user/**","/js/user/**","/pages/user/**",
                                "/js/**", "/pages/**", "/js/common/**"
                        ).permitAll()
                        // ALL
                        .requestMatchers("/api/user/login",
                                         "/api/admin/register","/api/admin/login",
                                         "/admin", "/adminRegister").permitAll()

                        // 관리자 (ROLE_ADMIN)
                        .requestMatchers("/api/admin/userRegister","/api/admin/userUpdate",
                                        "/api/admin/logout","/api/admin/reAccessToken",
                                        "/adminDashboard", "/userRegister").hasRole("ADMIN")

                        // 사원 (ROLE_USER)
                        .requestMatchers("/api/user/logout","/api/user/password", "/api/user/reAccessToken").hasRole("USER")


                    .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
