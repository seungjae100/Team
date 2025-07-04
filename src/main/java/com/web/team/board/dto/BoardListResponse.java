package com.web.team.board.dto;

import com.web.team.board.domain.Board;
import com.web.team.board.domain.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BoardListResponse {

    private String uuid;
    private String title;
    private String content;
    private String writerName;
    private LocalDateTime createdAt;
    private BoardStatus status;

    public static BoardListResponse from(Board board) {
        return new BoardListResponse(
                board.getUuid(),
                board.getTitle(),
                board.getContent(),
                board.getWriter().getName(),
                board.getCreatedAt(),
                board.getBoardStatus()
        );
    }
}
