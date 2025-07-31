package com.web.team.config;

import com.web.team.admin.repository.AdminRepository;
import com.web.team.jwt.JwtAuthenticationFilter;
import com.web.team.jwt.JwtTokenProvider;
import com.web.team.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger
                        .requestMatchers(
                            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                            "/swagger-resources/**", "/webjars/**"
                        ).permitAll()

                        // 비인증 접근 허용
                        .requestMatchers("/api/user/login",
                                         "/api/admin/register","/api/admin/login"
                        ).permitAll()

                        // 공지사항 - 관리자만 작성, 수정, 삭제
                        .requestMatchers(HttpMethod.POST, "/api/board/create").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/board/{uuid}/images").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/board/{uuid}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/board/{uuid}").hasRole("ADMIN")

                        // 공지사항 - 로그인 유저는 조회만 가능
                        .requestMatchers(HttpMethod.GET, "/api/board").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/board/{uuid}").authenticated()

                        // 채팅 - 로그인한 직원들의 채팅 기능
                        .requestMatchers(HttpMethod.POST, "/api/chat/rooms/direct").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/chat/rooms/group").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/chat/rooms/*/enter").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/chat/rooms/*/exit").hasRole("USER")

                        // 직원 계정 관련
                        .requestMatchers(HttpMethod.POST,"/api/employees/register").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/employees/update").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/employees").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/employees/*").hasAnyRole("ADMIN", "USER")
                        
                        // 일정 관련
                        .requestMatchers(HttpMethod.POST, "/api/schedules/company").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/schedules/company").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/schedules/company/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/schedules/company/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/schedules/company/*").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/schedule/employee").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/schedule/employee").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/schedule/employee/*").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/schedule/employee/*").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/schedule/employee/*").hasRole("USER")

                        // 관리자 (ROLE_ADMIN)
                        .requestMatchers("/api/admin/").hasRole("ADMIN")
                                        
                        // 사원 (ROLE_USER)
                        .requestMatchers("/api/user/").hasRole("USER")


                    .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider,adminRepository, userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
