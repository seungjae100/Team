package com.web.team.board.dto;

import com.web.team.board.domain.BoardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "공지사항 수정 요청 DTO")
public class BoardUpdateRequest {

    @Schema(description = "공지사항 제목", example = "대체공휴일 휴무 일정안내")
    private String title;

    @Schema(description = "공지사항 내용", example = "1월 1일 공휴일이 주말인 관계로...")
    private String content;

    @Schema(description = "공지사항 상태", example = "ACTIVE")
    private BoardStatus status;

}
