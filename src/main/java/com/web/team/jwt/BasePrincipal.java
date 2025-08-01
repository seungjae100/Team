package com.web.team.jwt;

import com.web.team.user.domain.Role;

public interface BasePrincipal {

    Role getRole();
    String getLoginId();
    
}
