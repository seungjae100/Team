package com.web.team.board.dto;

import com.web.team.board.domain.Board;
import com.web.team.board.domain.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class BoardDetailResponse {

    private String uuid;
    private String title;
    private String content;
    private String writerName;
    private LocalDateTime createdAt;
    private BoardStatus status;
    private List<BoardImageResponse> images;

    public static BoardDetailResponse from(Board board) {
        List<BoardImageResponse> imagesResponse = board.getImages().stream()
                .map(BoardImageResponse::from)
                .toList();


        return new BoardDetailResponse(
                board.getUuid(),
                board.getTitle(),
                board.getContent(),
                board.getWriter().getName(),
                board.getCreatedAt(),
                board.getBoardStatus(),
                imagesResponse
        );
    }
}
