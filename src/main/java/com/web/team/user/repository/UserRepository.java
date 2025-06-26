package com.web.team.user.repository;

import com.web.team.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    // 중복된 이메일이 있는지 확인
    boolean existsByEmail(String email);

    // 활성화된 유저들만 찾는
    List<User> findByIsActiveTrue();

}
