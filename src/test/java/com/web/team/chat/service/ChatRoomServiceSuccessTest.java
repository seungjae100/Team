package com.web.team.chat.service;

import com.web.team.chat.domain.ChatParticipant;
import com.web.team.chat.domain.ChatRoom;
import com.web.team.chat.domain.RoomType;
import com.web.team.chat.dto.ChatRoomResponse;
import com.web.team.chat.dto.DirectChatRoomCreateRequest;
import com.web.team.chat.dto.GroupChatRoomCreateRequest;
import com.web.team.chat.repository.ChatParticipantRepository;
import com.web.team.chat.repository.ChatRoomRepository;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.domain.User;
import com.web.team.user.repository.UserRepository;

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

    @Mock
    private UserRepository userRepository;


    @Test
    @DisplayName("1:1 채팅방 생성 성공")
    void createDirectChatRoom_success() {

        // given
        Long currentId = 1L;
        Long anotherId = 2L;

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(currentId);
        
        // user mock 설정
        User currentUser = new User();
        ReflectionTestUtils.setField(currentUser, "id", currentId);
        when(userRepository.findById(currentId)).thenReturn(Optional.of(currentUser));

        User anotherUser = new User();
        ReflectionTestUtils.setField(anotherUser, "id", anotherId);
        when(userRepository.findById(anotherId)).thenReturn(Optional.of(anotherUser));
        
        // 기존 채팅방 없음 설정
        when(chatRoomRepository.findDirectRoom(currentId, anotherId))
            .thenReturn(Optional.empty());

        // 저장 동작은 그대로 객체 리턴하도록 처리
        when(chatRoomRepository.save(any(ChatRoom.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

        
        

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
        List<Long> userIds = List.of(2L, 3L);
        GroupChatRoomCreateRequest request = new GroupChatRoomCreateRequest(name, userIds);

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(1L);

        
        // 사용자 모킹
    User creator = new User();
    ReflectionTestUtils.setField(creator, "id", 1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(creator));

    User user2 = new User();
    ReflectionTestUtils.setField(user2, "id", 2L);
    when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

    User user3 = new User();
    ReflectionTestUtils.setField(user3, "id", 3L);
    when(userRepository.findById(3L)).thenReturn(Optional.of(user3));

    // 저장 Mock
    when(chatRoomRepository.save(any(ChatRoom.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

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
        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(chatParticipantRepository.findByRoomIdAndUserId(roomId, userId)).thenReturn(Optional.empty());
        

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


        // when
        chatRoomService.exitRoom(roomId, userDetails);

    }


}
