package com.web.team.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Schema(description = "1:1 채팅방을 생성하는 요청 DTO 입니다.")
public class DirectChatRoomCreateRequest {

    @Schema(description = "1:1 채팅방의 상대방 ID", example = "상대방 이름")
    private Long anotherId; // 상대방 아이디
}
