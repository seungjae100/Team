package com.web.team.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class GroupChatRoomCreateRequest {

    private String name; // 채팅방 이름
    private List<Long> userIds; // 초대할 사용자 ID 목록들
}
