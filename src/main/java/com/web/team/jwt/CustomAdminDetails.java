package com.web.team.jwt;

import com.web.team.admin.domain.Admin;
import com.web.team.user.domain.Role;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class CustomAdminDetails implements UserDetails, BasePrincipal {

    private final Admin admin;

    @Override
    public Role getRole() {
        return admin.getRole();
    }

    @Override
    public String getLoginId() {
        return admin.getUsername();
    }

     public Admin getAdmin() {
        return admin;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public String getUsername() {
        return admin.getUsername();
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRole().name()));
    }
}
