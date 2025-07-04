package com.web.team.board.dto;

import com.web.team.board.domain.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardUpdateRequest {

    private String title;

    private String content;

    private BoardStatus status;

}
