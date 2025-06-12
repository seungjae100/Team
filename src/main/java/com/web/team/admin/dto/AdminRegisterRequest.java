package com.web.team.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminRegisterRequest {

    private String username;
    private String password;
    private String name;

}
