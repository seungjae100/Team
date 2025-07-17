package com.web.team.chat.service;

import com.web.team.chat.domain.ChatParticipant;
import com.web.team.chat.domain.ChatRoom;
import com.web.team.chat.domain.RoomType;
import com.web.team.chat.dto.ChatRoomResponse;
import com.web.team.chat.dto.DirectChatRoomCreateRequest;
import com.web.team.chat.dto.GroupChatRoomCreateRequest;
import com.web.team.chat.repository.ChatMessageRepository;
import com.web.team.chat.repository.ChatParticipantRepository;
import com.web.team.chat.repository.ChatRoomRepository;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceSuccessTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatParticipantRepository chatParticipantRepository;


    @Test
    @DisplayName("1:1 채팅방 생성 성공")
    void createDirectChatRoom_success() {

        // given
        Long currentId = 1L;
        Long anotherId = 2L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(currentId);

        when(chatRoomRepository.findDirectRoom(currentId, anotherId))
                .thenReturn(Optional.empty());

        ChatRoom room = ChatRoom.create("1:1 채팅", RoomType.DIRECT);

        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(room);
        when(chatParticipantRepository.save(any(ChatParticipant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DirectChatRoomCreateRequest request = new DirectChatRoomCreateRequest(anotherId);

        // when
        ChatRoomResponse result = chatRoomService.createDirectChatRoom(request, userDetails);

        // then
        assertNotNull(result);
        assertEquals("1:1 채팅", result.name());
        assertEquals("DIRECT", result.type());


    }

    @Test
    @DisplayName("그룹 채팅방 생성 성공")
    void createGroupChatRoom() {
        // given
        String name = "그룹 채팅방 1";
        List<Long> userIds = List.of(1L, 2L, 3L);

        GroupChatRoomCreateRequest request = new GroupChatRoomCreateRequest(name, userIds);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(100L);

        ChatRoom room = ChatRoom.create(name, RoomType.GROUP);
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(room);
        when(chatParticipantRepository.save(any(ChatParticipant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ChatRoomResponse result = chatRoomService.createGroupChatRoom(request, userDetails);

        // then
        assertNotNull(result);
        assertEquals(name, result.name());
        assertEquals("GROUP", result.type());
    }

    @Test
    @DisplayName("채팅방 입장 성공")
    void enterRoom() {
        // given
        Long roomId = 1L;
        Long userId = 10L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(userId);

        ChatRoom room = ChatRoom.create("그룹채팅", RoomType.GROUP);
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(chatParticipantRepository.findByRoomIdAndUserId(roomId, userId)).thenReturn(Optional.empty());
        when(chatParticipantRepository.save(any(ChatParticipant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        chatRoomService.enterRoom(roomId, userDetails);

    }

    @Test
    @DisplayName("채팅방 나가기 / 삭제")
    void exitRoom_success_and_delete() {
        // given
        Long roomId = 1L;
        Long userId = 10L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(userId);

        ChatRoom room = ChatRoom.create("그룹채팅", RoomType.GROUP);
        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        ChatParticipant participant = ChatParticipant.enter(room, user);

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(chatParticipantRepository.findByRoomIdAndUserId(roomId, userId)).thenReturn(Optional.of(participant));
        when(chatParticipantRepository.allUserExited(roomId)).thenReturn(true); // 마지막 유저

        // when
        chatRoomService.exitRoom(roomId, userDetails);

    }


}
