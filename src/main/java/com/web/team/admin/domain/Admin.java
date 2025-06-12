package com.web.team.admin.domain;

import com.web.team.user.domain.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "admin")
public class Admin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String name;

    private Role role;

    // 관리자의 직원 등록
    public static Admin create(String username, String password, String name) {
        Admin admin = new Admin();
        admin.username = username;
        admin.password = password;
        admin.role = Role.ADMIN;
        admin.name = name;

        return admin;
    }
}


