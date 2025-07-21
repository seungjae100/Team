package com.web.team.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.web.team.chat.domain.ChatRoom;
import com.web.team.chat.domain.RoomType;
import com.web.team.chat.dto.ChatMessageRequest;
import com.web.team.chat.redis.RedisPublisher;
import com.web.team.chat.repository.ChatMessageRepository;
import com.web.team.chat.repository.ChatRoomRepository;
import com.web.team.user.domain.Position;
import com.web.team.user.domain.User;
import com.web.team.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceFailTest {

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisPublisher redisPublisher;

    @Test
    @DisplayName("메세지 전송 실패 - 메세지 내용 없음")
    void sendMessage_fail_blankMessage() {
        ChatMessageRequest request = new ChatMessageRequest(1L, null);
        Long userId = 1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            chatMessageService.sendMessage(request, userId);
        });
        
        assertEquals("메세지 내용은 비어 있을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("메세지 전송 실패 - roomId 없음")
    void sendMessage_fail_nullRoomId() {
        ChatMessageRequest request = new ChatMessageRequest(null, "안녕");
        Long userId = 1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            chatMessageService.sendMessage(request, userId)
        );

        assertEquals("채팅방 ID가 필요합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("메세지 전송 실패 - 사용자 없음")
    void sendMessage_fail_userNotFound() {
        ChatMessageRequest request = new ChatMessageRequest(1L, "메세지");
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            chatMessageService.sendMessage(request, userId)
        );

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("메세지 전송 실패 - 채팅방 없음")
    void sendMessage_fail_roomNotFound() {
        ChatMessageRequest request = new ChatMessageRequest(999L, "메세지");
        Long userId = 1L;

        User user = User.create("user@gmail.com", "password1234", "신짱구", Position.STAFF);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            chatMessageService.sendMessage(request, userId)
        );

        assertEquals("채팅방을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("메세지 조회 실패 - 채팅방 없음")
    void getMessage_fail_roomNotFound() {
        Long roomId = 1L;
        Long userId = 1L;

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            chatMessageService.getMessageByRoomId(roomId, userId, 0, 10)
        );

        assertEquals("채팅방이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("메세지 조회 실패 - size가 100 초과")
    void getMessage_fail_sizeTooLarge() {
        Long roomId = 1L;
        Long userId = 1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            chatMessageService.getMessageByRoomId(roomId, userId, 0, 101)
        );

        assertEquals("한 번에 조회할 수 있는 메세지 수는 100개 입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("메세지 조회 실패 - 참여자가 아님")
    void getMessage_fail_notParticipant() {
        Long roomId = 1L;
        Long userId = 1L;

        ChatRoom chatRoom = ChatRoom.create("그룹채팅", RoomType.GROUP);

        // 참여자가 없음
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> 
            chatMessageService.getMessageByRoomId(roomId, userId, 0, 10)
        );

        assertEquals("해당 채팅방의 참여자만 메세지를 조회할 수 있습니다.", exception.getMessage());
    }
}