package com.web.team.user.dto;

import com.web.team.user.domain.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {

    private String name;
    private Position position;
    private Boolean isActive;
}
