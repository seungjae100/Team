package com.web.team.user.repository;

import com.web.team.user.domain.User;

import java.util.Optional;

public interface UserRepositoryCustom {

    Optional<User> findActiveUserByEmail(String email);

}
