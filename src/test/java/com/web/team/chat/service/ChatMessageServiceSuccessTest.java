package com.web.team.chat.service;

import com.web.team.chat.domain.ChatMessage;
import com.web.team.chat.dto.ChatMessageRequest;
import com.web.team.chat.dto.ChatMessageResponse;
import com.web.team.chat.repository.ChatMessageRepository;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.domain.Position;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceSuccessTest {

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Test
    @DisplayName("채팅 메세지 전송 성공")
    void sendMessage_success() {
        // given
        Long userId = 1L;
        Long roomId = 100L;
        String message = "안녕하세요";
        String username = "김지은";

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(userId);

        User user = User.create( "test@gmail.com", "password", username, Position.INTURN);
        ReflectionTestUtils.setField(user, "id", userId);

        ChatMessageRequest request = new ChatMessageRequest(roomId, message);

        ChatMessage chatMessage = ChatMessage.create(roomId, userId, username, message);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

        // when
        ChatMessageResponse response = chatMessageService.sendMessage(request, userDetails);

        // then
        assertEquals(roomId, response.getRoomId());
        assertEquals(userId, response.getSenderId());
        assertEquals(username, response.getSenderName());
        assertEquals(message, response.getMessage());
    }

    @Test
    @DisplayName("채팅 메세지 조회 성공")
    void getMessageByRoomId_success() {
        // given
        Long roomId = 100L;

        // 1 개의 방에 2 명의 유저가 서로 메세지를 전송한다.
        ChatMessage message1 = ChatMessage.create(roomId, 1L, "김지은", "안녕하세요");
        ChatMessage message2 = ChatMessage.create(roomId, 2L, "김민수", "반갑습니다.");
        // 2 개의 메세지를 List로 묶어서 messageList 변수명에 저장한다.
        List<ChatMessage> messageList = List.of(message1, message2);
        // 메세지 저장소에서 채팅방에 오름차순 순서대로 저장된 메세지를 순서대로 List화 한다.
        when(chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId)).thenReturn(messageList);


        // when
        List<ChatMessageResponse> responses = chatMessageService.getMessageByRoomId(roomId);

        // then
        assertEquals(2, responses.size());
        assertEquals("김지은", responses.get(0).getSenderName());
        assertEquals("반갑습니다.", responses.get(1).getMessage());
    }

    @Test
    @DisplayName("채팅 메세지 삭제 성공")
    void deleteMessageByRoomId_success() {
        // given
        Long roomId = 100L;

        // when
        chatMessageService.deletedMessageByRoomId(roomId);

        // then
        verify(chatMessageRepository).deleteByRoomId(roomId);
    }


}
