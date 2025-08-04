package com.web.team.board.dto;

import com.web.team.board.domain.BoardImage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Base64;
@Schema(description = "공지사항 이미지 파일 응답 DTO")
public record BoardImageResponse(

        @Schema(description = "이미지 파일 이름", example = "banana.png")
        String originalFilename,

        @Schema(description = "MINE 타입", example = "image/png")
        String contentType,

        @Schema(description = "Base64로 인코딩된 이미지", example = "ivVVDEfewfdafekAA...")
        String base64Data
) {
    public static BoardImageResponse from(BoardImage image) {
        return new BoardImageResponse(
                image.getOriginalFilename(),
                image.getContentType(),
                Base64.getEncoder().encodeToString(image.getData())
        );
    }


}
