package com.web.team.chat.service;

import com.web.team.chat.dto.ChatMessageRequest;
import com.web.team.chat.repository.ChatMessageRepository;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceFailTest {

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Test
    @DisplayName("채팅 메세지 전송 실패 - 사용자 없음")
    void sendMessage_fail_userNotFound() {
        // given
        Long userId = 1L;
        Long roomId = 100L;
        String message = "메세지 전송가는중";

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(userId);

        ChatMessageRequest request = new ChatMessageRequest(roomId, message);

        // 사용자 조회 시 실패
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            chatMessageService.sendMessage(request, userDetails);
        });
    }


}
