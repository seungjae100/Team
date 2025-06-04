package com.web.team.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본키

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (아이디)

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String name; // 직원명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ADMIN, USER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Position position; // 임원, 부장, 과장, 팀장, 대리, 주임, 사원, 인턴

    @Column(nullable = false)
    private boolean isActive; // 계정 활성화/비활성화

    // 관리자의 직원 등록
    // 관리자의 직원 계정 활성화/비활성화 변경
    // 관리자의 직급 변경 (ON/OFF)
    // 관리자의 직원 조회 (비활성화한 계정은 보이지 않게)

    // 직원의 본인 비밀번호 변경
    // 직원의 본인 정보 조회








}
