package com.web.team.chat.controller;

import com.web.team.chat.dto.ChatRoomResponse;
import com.web.team.chat.dto.DirectChatRoomCreateRequest;
import com.web.team.chat.dto.GroupChatRoomCreateRequest;
import com.web.team.chat.service.ChatRoomService;
import com.web.team.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
@Tag(name = "채팅방 API", description = "채팅방 생성, 입장, 퇴장 관련 API 입니다.")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "1:1 채팅방 생성", description = "다른 유저와 1:1 채팅방을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "1:1 채팅방 생성 성공"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PostMapping("/direct")
    public ChatRoomResponse createDirectRoom(@RequestBody DirectChatRoomCreateRequest request,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        return chatRoomService.createDirectChatRoom(request, userDetails);
    }

    @Operation(summary = "그룹 채팅방 생성", description = "여러 명과 그룹 채팅방을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 채팅방 생성 성공"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PostMapping("/group")
    public ChatRoomResponse createGroupRoom(@RequestBody GroupChatRoomCreateRequest request,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        return chatRoomService.createGroupChatRoom(request, userDetails);
    }

    @Operation(summary = "채팅방 입장", description = "채팅방에 입장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 입장 성공"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PostMapping("/{roomId}/enter")
    public void enterRoom(@PathVariable Long roomId,
                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatRoomService.enterRoom(roomId, userDetails);
    }

    @Operation(summary = "채팅방 퇴장", description = "채팅방을 퇴장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 퇴장 성공"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PostMapping("/{roomId}/exit")
    public void exitRoom(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatRoomService.exitRoom(roomId, userDetails);
    }

}
