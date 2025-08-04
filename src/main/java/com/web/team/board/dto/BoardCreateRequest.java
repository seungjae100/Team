package com.web.team.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "공지사항 작성 요청 DTO")
public class BoardCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Schema(description = "공지사항 제목", example = "대체공휴일 휴무 일정안내")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Schema(description = "공지사항 내용", example = "1월 1일 공휴일이 주말인 관계로...")
    private String content;

}
