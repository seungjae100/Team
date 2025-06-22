package com.web.team.chat.service;

import com.web.team.chat.domain.ChatMessage;
import com.web.team.chat.domain.ChatRoom;
import com.web.team.chat.dto.ChatMessageRequest;
import com.web.team.chat.dto.ChatMessageResponse;
import com.web.team.chat.repository.ChatMessageRepository;
import com.web.team.chat.repository.ChatRoomRepository;
import com.web.team.chat.repository.ChatRoomRepositoryCustom;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.domain.User;
import com.web.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 채팅 메세지 저장
     */
    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageRequest request, CustomUserDetails userDetails) {
        // 사용자의 user_id 값을 가져온다.
        Long userId = userDetails.getUserId();

        // 사용자를 데이터베이스에 있는지 조회한다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 메세지 생성 및 저장 (ChatMessage 엔티티 메서드 매개변수 순서)
        ChatMessage chatMessage = ChatMessage.create(
                chatRoom,
                user,
                request.getMessage()
        );

        ChatMessage saved = chatMessageRepository.save(chatMessage);
        return ChatMessageResponse.from(saved);
    }

    /**
     * 특정 채팅방의 모든 메세지 조회
     */
    @Transactional
    public List<ChatMessageResponse> getMessageByRoomId(Long roomId) {

        chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId)
                .stream()
                .map(ChatMessageResponse::from)
                .collect(Collectors.toList());
    }

}
