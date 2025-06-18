package com.web.team.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DirectChatRoomCreateRequest {

    private Long anotherId; // 상대방 아이디
}
