package com.web.team.user.dto;

import lombok.AllArgsConstructor;
import com.web.team.user.domain.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserRegisterRequest {

    private String email;
    private String password;
    private String name;
    private Position position;
}
