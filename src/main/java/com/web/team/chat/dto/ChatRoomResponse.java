package com.web.team.chat.dto;

import com.web.team.chat.domain.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 응답 DTO")
public record ChatRoomResponse(

    @Schema(description = "채팅방 ID", example = "1 , 보안 리팩토링 예정")
    Long roomId,

    @Schema(description = "생성될 그룹 채팅방 이름", example = "그룹 채팅방 1")
    String name,

    @Schema(description = "채팅방 타입", example = "DIRECT")
    String type

) {
    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getRoomType().name()
        );
    }
}
