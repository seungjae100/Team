package com.web.team.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "그룹 채팅방 생성 요청 DTO")
public class GroupChatRoomCreateRequest {

    @Schema(description = "생성될 그룹 채팅방 이름", example = "그룹 채팅방 1")
    private String name; // 채팅방 이름

    @Schema(description = "참가할 유저들의 목록")
    private List<Long> userIds; // 초대할 사용자 ID 목록들
}
