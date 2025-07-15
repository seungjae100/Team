package com.web.team.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메세지 전송을 요청하는 DTO 입니다.")
public class ChatMessageRequest {

    @Schema(description = "채팅 메세지가 작성된 채팅방 ID", example = "12")
    private Long roomId;

    @Schema(description = "채팅 메세지", example = "안녕하세요 짱구님")
    private String message;

}
