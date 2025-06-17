package com.web.team.chat.service;

import com.web.team.chat.domain.ChatParticipant;
import com.web.team.chat.domain.ChatRoom;
import com.web.team.chat.repository.ChatMessageRepository;
import com.web.team.chat.repository.ChatParticipantRepository;
import com.web.team.chat.repository.ChatRoomRepository;
import com.web.team.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;


    // 채팅방 입장
    @Transactional
    public void enterRoom(Long roomId, CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 2. 참여자 기록 확인
        chatParticipantRepository.findByRoomIdAndUserId(roomId, userId)
                .ifPresentOrElse(
                        chatParticipant -> {
                            if (chatParticipant.isExited()) {
                                chatParticipant.reEnter();
                                chatRoom.increaseUserCount();
                            }
                        },
                        () -> {
                            ChatParticipant newParticipant = ChatParticipant.enter(roomId, userId);
                            chatParticipantRepository.save(newParticipant);
                            chatRoom.increaseUserCount();
                        }
                );
    }

    // 채팅방 나가기
    @Transactional
    public void exitRoom(Long roomId, CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        // 1. 채팅방 조회
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 없습니다."));

        // 2. 참여자 조회 및 퇴장처리
        ChatParticipant participant = chatParticipantRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("참여자를 찾을 수 없습니다."));

        if (!participant.isExited()) {
            participant.exit(); // 퇴장처리, 퇴장시간은 처리시간
            room.decreaseUserCount(); //
        }

        // 2. 모든 참여자 퇴장 여부 확인 - 모두 나간 경우 채팅방을 삭제
        boolean allExited = chatParticipantRepository.allUserExited(roomId);
        if (allExited) {
            chatParticipantRepository.deleteByRoomId(roomId);
            chatMessageRepository.deleteByRoomId(roomId);
            chatRoomRepository.deleteById(roomId);
        }
    }
}
