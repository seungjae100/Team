package com.web.team.chat.service;

import com.web.team.chat.domain.ChatMessage;
import com.web.team.chat.domain.ChatRoom;
import com.web.team.chat.dto.ChatMessageRequest;
import com.web.team.chat.dto.ChatMessageResponse;
import com.web.team.chat.redis.RedisPublisher;
import com.web.team.chat.repository.ChatMessageRepository;
import com.web.team.chat.repository.ChatRoomRepository;
import com.web.team.chat.repository.ChatRoomRepositoryCustom;
import com.web.team.jwt.CustomUserDetails;
import com.web.team.user.domain.User;
import com.web.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
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
    private final RedisPublisher redisPublisher;

    /**
     * 채팅 메세지 저장 + 반환
     */
    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageRequest request, Long userId) {
        // 사용자를 조회한다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 채팅방을 조회한다.
        ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 메세지 생성 및 저장 (ChatMessage 엔티티 메서드 매개변수 순서)
        ChatMessage chatMessage = ChatMessage.create(chatRoom, user, request.getMessage());
        ChatMessage saved = chatMessageRepository.save(chatMessage);

        ChatMessageResponse response = ChatMessageResponse.from(saved);

        // Redis 채널로 메세지 발행
        redisPublisher.publish(chatRoom.getId(), response);

        return response;

    }

    /**
     * 특정 채팅방의 모든 메세지 조회
     */
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessageByRoomId(Long roomId, Long userId, int page, int size) {
        // 채팅방 존재 확인
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // 참여자 검증
        boolean isParticipant = chatRoom.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(userId));

        if (!isParticipant) {
            throw new AccessDeniedException("해당 채팅방의 참여자만 메세지를 조회할 수 있습니다.");
        }

        // 페이징 메시지 조회
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        Page<ChatMessage> pageMessages = chatMessageRepository.findByChatRoomId(roomId, pageRequest);


        return pageMessages.getContent().stream()
                .map(ChatMessageResponse::from)
                .collect(Collectors.toList());
    }

}
