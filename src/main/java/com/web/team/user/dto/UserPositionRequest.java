package com.web.team.user.dto;

import com.web.team.user.domain.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserPositionRequest {

    private Position position;
}
