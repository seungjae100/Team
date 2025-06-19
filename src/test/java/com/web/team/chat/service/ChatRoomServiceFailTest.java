package com.web.team.chat.service;

import com.web.team.chat.domain.ChatRoom;
import com.web.team.chat.domain.RoomType;
import com.web.team.chat.dto.ChatRoomResponse;
import com.web.team.chat.repository.ChatMessageRepository;
import com.web.team.chat.repository.ChatParticipantRepository;
import com.web.team.chat.repository.ChatRoomRepository;
import com.web.team.jwt.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceFailTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatParticipantRepository chatParticipantRepository;

    @Test
    @DisplayName("1:1 채팅방 생성 실패")
    void createDirectChatRoom_fail() {
        // given
        Long currentId = 1L;
        Long anotherId = 2L;

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(currentId);

        ChatRoom room = ChatRoom.create("1:1 채팅", RoomType.DIRECT);
        when(chatRoomRepository.findByDirectRoomByUsers(currentId, anotherId))
                .thenReturn(Optional.ofNullable(room));

        // when
        ChatRoomResponse result = chatRoomService.createDirectChatRoom(anotherId, userDetails);

        // then
        assertNotNull(result);
        assertEquals("1:1 채팅", result.getName());
        assertEquals("DIRECT", result.getType());
    }

    @Test
    @DisplayName("그룹 채팅방 생성 실패 - 참여자가 1명도 없음")
    void createGroupChatRoom_fail_noUsers() {
        // given
        String roomName = "그룹 채팅방";
        List<Long> emptyUserList = List.of(); // 빈 리스트 - 유저 없음

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> chatRoomService.createGroupChatRoom(roomName, emptyUserList),
                "참여자는 한 명 이상이여야 합니다.");
    }

    @Test
    @DisplayName("채팅방 입장 실패 - 존재하지 않는 채팅방")
    void enterRoom_fail_roomNotFound() {
        // given
        Long roomId = 999L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> chatRoomService.enterRoom(roomId, userDetails),
                "채팅방을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("채팅방 나가기 실패 - 채팅방 없음")
    void exitRoom_fail_roomNotFound() {
        // given
        Long roomId = 999L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> chatRoomService.exitRoom(roomId, userDetails),
                "채팅방이 없습니다.");

    }

    @Test
    @DisplayName("채팅방 나가기 실패 - 참여자 없음")
    void exitRoom_fail_participantNotFound() {

        // given
        Long roomId = 1L;
        Long userId = 2L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(userId);

        ChatRoom room = ChatRoom.create("채팅방", RoomType.GROUP);
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(chatParticipantRepository.findByRoomIdAndUserId(roomId, userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> chatRoomService.exitRoom(roomId, userDetails),
                "참여자를 찾을 수 없습니다.");

    }
}
