package com.web.team.board.dto;

import com.web.team.board.domain.Board;
import com.web.team.board.domain.BoardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "공지사항 상세내용 응답 DTO")
public record BoardDetailResponse(

    @Schema(description = "공지사항 UUID", example = "1cfebw32kfewjsf03f")
    String uuid,

    @Schema(description = "공지사항 제목", example = "대체공휴일 휴무 일정안내")
    String title,

    @Schema(description = "공지사항 내용", example = "1월 1일 공휴일이 주말인 관계로...")
    String content,

    @Schema(description = "공지사항 작성자", example = "관리자")
    String writerName,

    @Schema(description = "공지사항 작성일", example = "2025-07-25")
    LocalDateTime createdAt,

    @Schema(description = "공지사항 상태", example = "ACTIVE")
    BoardStatus status,

    @Schema(description = "첨부 이미지")
    List<BoardImageResponse> images

) {
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
