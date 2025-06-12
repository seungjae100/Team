package com.web.team.user.controller;

import com.web.team.user.dto.UserLoginRequest;
import com.web.team.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // 직원 계정 로그인
    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@RequestBody UserLoginRequest request) {
        userService.userLogin(request);
        return ResponseEntity.ok("로그인이 완료되었습니다.");
    }

}
