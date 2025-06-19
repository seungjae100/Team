package com.web.team.chat.service;

import com.web.team.chat.domain.ChatParticipant;
import com.web.team.chat.domain.ChatRoom;
import com.web.team.chat.domain.RoomType;
import com.web.team.chat.dto.ChatRoomResponse;
import com.web.team.chat.repository.ChatMessageRepository;
import com.web.team.chat.repository.ChatParticipantRepository;
import com.web.team.chat.repository.ChatRoomRepository;
import com.web.team.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 1:1 채팅방 생성하기
    @Transactional
    public ChatRoomResponse createDirectChatRoom(Long anotherId, CustomUserDetails userDetails) {
        Long currentId = userDetails.getUserId();

        // 1. 두 유저가 참여한 DIRECT 채팅방에 있는지 확인
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByDirectRoomByUsers(currentId, anotherId);

        if (existingRoom.isPresent()) {
            return ChatRoomResponse.of(existingRoom.get().getId(), existingRoom.get().getName(), existingRoom.get().getRoomType().name());
        }

        // 2. 없다면 생성
        ChatRoom room = ChatRoom.create("1:1 채팅", RoomType.DIRECT);
        chatRoomRepository.save(room);

        // 3. 두 사용자 모두 참여자 등록
        chatParticipantRepository.save(ChatParticipant.enter(room.getId(), currentId));
        chatParticipantRepository.save(ChatParticipant.enter(room.getId(), anotherId));

        return ChatRoomResponse.of(room.getId(), room.getName(), room.getRoomType().name());
    }

    // 그룹 채팅방 생성하기
    @Transactional
    public ChatRoom createGroupChatRoom(String name, List<Long> userIds) {
        // 그룹채팅을 위해서 사용자를 추가하는 상황인데 한 명도 체크하지 않고 채팅방 생성을 하려할 때의 예외처리
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("참여자는 한 명 이상이여야 합니다.");
        }
        // 채팅방 생성 (이름 , 그룹채팅타입)
        // 데이터베이스에 생성된 채팅방 정보 저장
        ChatRoom room = ChatRoom.create(name, RoomType.GROUP);
        ChatRoom savedRoom = chatRoomRepository.save(room);

        // 타입은 userId , 반복한다. 참여자 엔티티에서 데이터베이스에 저장된 채팅방의 아이디, 참여자 아이디
        // 참여자 정보를 저장한다.
        // 데이터베이스에 저장된 참여자 인원 수를 늘린다.
        for (Long userId : userIds) {
            ChatParticipant participant = ChatParticipant.enter(savedRoom.getId(), userId);
            chatParticipantRepository.save(participant);
            savedRoom.increaseUserCount();
        }

        return savedRoom;
    }


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
