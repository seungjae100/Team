package com.web.team.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "메세지를 조회하는 요청 DTO")
public class MessageLoadRequest {

    @Schema(description = "채팅방 ID", example = "1, 리팩토링 예정(보안)")
    private Long roomId;

    @Schema(description = "페이지 번호", example = "1")
    private int page;

    @Schema(description = "페이지 크기 한 번에 조회할 메세지 수", example = "20")
    private int size;
}
