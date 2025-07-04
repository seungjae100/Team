package com.web.team.board.dto;

import com.web.team.board.domain.BoardImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Base64;

@Getter
@AllArgsConstructor
public class BoardImageResponse {
    private String originalFilename;
    private String contentType;
    private String base64Data;

    public static BoardImageResponse from(BoardImage image) {
        return new BoardImageResponse(
                image.getOriginalFilename(),
                image.getContentType(),
                Base64.getEncoder().encodeToString(image.getData())
        );
    }


}
