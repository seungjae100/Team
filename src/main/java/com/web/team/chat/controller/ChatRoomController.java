package com.web.team.chat.controller;

import com.web.team.chat.dto.ChatRoomResponse;
import com.web.team.chat.dto.DirectChatRoomCreateRequest;
import com.web.team.chat.dto.GroupChatRoomCreateRequest;
import com.web.team.chat.service.ChatRoomService;
import com.web.team.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 1:1 채팅방 생성
    @PostMapping("/direct")
    public ChatRoomResponse createDirectRoom(@RequestBody DirectChatRoomCreateRequest request,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        return chatRoomService.createDirectChatRoom(request, userDetails);
    }

    // 그룹 채팅방 생성
    @PostMapping("/group")
    public ChatRoomResponse createGroupRoom(@RequestBody GroupChatRoomCreateRequest request,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        return chatRoomService.createGroupChatRoom(request, userDetails);
    }

    // 채팅방 입장
    @PostMapping("/{roomId}/enter")
    public void enterRoom(@PathVariable Long roomId,
                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatRoomService.enterRoom(roomId, userDetails);
    }

    // 채팅방 퇴장
    @PostMapping("/{roomId}/exit")
    public void exitRoom(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatRoomService.exitRoom(roomId, userDetails);
    }

}
