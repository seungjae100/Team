package com.web.team.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomResponse {

    private Long roomId;
    private String name;
    private String type;

    public static ChatRoomResponse of(Long roomId, String name, String type) {
        return new ChatRoomResponse(roomId, name, type);
    }
}
