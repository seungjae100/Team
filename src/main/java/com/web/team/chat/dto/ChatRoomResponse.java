package com.web.team.chat.dto;

import com.web.team.chat.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomResponse {

    private Long roomId;
    private String name;
    private String type;

    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getRoomType().name()
        );
    }
}
