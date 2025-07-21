package com.web.team.chat.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.web.team.chat.domain.ChatMessage;
import com.web.team.chat.domain.ChatParticipant;
import com.web.team.chat.domain.ChatRoom;
import com.web.team.chat.domain.RoomType;
import com.web.team.chat.dto.ChatMessageRequest;
import com.web.team.chat.dto.ChatMessageResponse;
import com.web.team.chat.redis.RedisPublisher;
import com.web.team.chat.repository.ChatMessageRepository;
import com.web.team.chat.repository.ChatRoomRepository;
import com.web.team.user.domain.Position;
import com.web.team.user.domain.User;
import com.web.team.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceSuccessTest {

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
    @DisplayName("메세지 전송 성공")
    void sendMessage_success() {
        // given
        Long userId = 1L;
        Long roomId = 100L;
        String content = "안녕 나는 짱구야";

        ChatMessageRequest request = new ChatMessageRequest(roomId, content);
        User user = User.create("user1@gmail.com", "password1234", "신짱구", Position.STAFF);
        ChatRoom chatRoom = ChatRoom.create("채팅방", RoomType.DIRECT);

        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(chatRoom, "id", roomId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.save(any(ChatMessage.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // when 
        ChatMessageResponse response = chatMessageService.sendMessage(request, userId);

        // then
        assertNotNull(response);
        assertEquals(content, response.message());
        assertEquals(userId, response.senderId());
        assertEquals(chatRoom.getId(), response.roomId());


    }

    @Test
    @DisplayName("메세지 조회 성공")
    void getMessage_success() {
        // given
        Long roomId = 1L;
        Long userId = 10L;

        User user = User.create("user@gmail.com", "password1234", "신짱구", Position.STAFF);
        ReflectionTestUtils.setField(user, "id", userId);

        ChatRoom chatRoom = ChatRoom.create("채팅방", RoomType.DIRECT);
        ReflectionTestUtils.setField(chatRoom, "id", roomId);

        // 참여자 연결
        ChatParticipant.enter(chatRoom, user);

        int page = 0;
        int size = 2;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));

        ChatMessage message1 = ChatMessage.create(chatRoom, user, "안녕");
        ChatMessage message2 = ChatMessage.create(chatRoom, user, "만나서 반가워 오랜만이네");

        Page<ChatMessage> pageMessage = new PageImpl<>(List.of(message1, message2), pageRequest, 2);

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findByChatRoomId(roomId, pageRequest)).thenReturn(pageMessage);

        // when
        List<ChatMessageResponse> result = chatMessageService.getMessageByRoomId(roomId, userId, page, size);


        // then
        assertEquals(2, result.size());
        assertEquals("안녕", result.get(0).message());
        assertEquals("만나서 반가워 오랜만이네", result.get(1).message());
        
    }    
}
