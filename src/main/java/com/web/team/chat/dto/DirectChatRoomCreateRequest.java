package com.web.team.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class DirectChatRoomCreateRequest {

    private Long anotherId; // 상대방 아이디
}
