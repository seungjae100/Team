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
import com.web.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    // 1:1 채팅방 생성하기
    @Transactional
    public ChatRoomResponse createDirectChatRoom(DirectChatRoomCreateRequest request, CustomUserDetails userDetails) {
        Long currentId = userDetails.getUserId();
        Long anotherId = request.getAnotherId();

        // 1. 두 유저가 참여한 DIRECT 채팅방에 있는지 확인
        Optional<ChatRoom> existingRoom = chatRoomRepository.findDirectRoom(currentId, anotherId);

        if (existingRoom.isPresent()) {
            return ChatRoomResponse.from(existingRoom.get());
        }

        // 2. 없다면 생성
        ChatRoom newRoom = ChatRoom.create("1:1 채팅", RoomType.DIRECT);
        User currentUser = userRepository.findById(currentId).orElseThrow();
        User anotherUser = userRepository.findById(anotherId).orElseThrow();

        ChatParticipant p1 = ChatParticipant.enter(newRoom, currentUser);
        ChatParticipant p2 = ChatParticipant.enter(newRoom, anotherUser);

        newRoom.addParticipant(p1);
        newRoom.addParticipant(p2);

        chatRoomRepository.save(newRoom);
        return ChatRoomResponse.from(newRoom);
    }

    // 그룹 채팅방 생성하기
    @Transactional
    public ChatRoomResponse createGroupChatRoom(GroupChatRoomCreateRequest request, CustomUserDetails userDetails) {
        Long creatorId = userDetails.getUserId();
        String roomName = request.getName();

        ChatRoom newRoom = ChatRoom.create(roomName, RoomType.GROUP);
        User creator = userRepository.findById(creatorId).orElseThrow();
        ChatParticipant creatorParticipant = ChatParticipant.enter(newRoom, creator);
        newRoom.addParticipant(creatorParticipant);

        for (Long userId : request.getUserIds()) {
            if (userId.equals(creatorId)) continue; // 본인은 참여
            User invitee = userRepository.findById(userId).orElseThrow();
            ChatParticipant participant = ChatParticipant.enter(newRoom, invitee);
            newRoom.addParticipant(participant);
        }

        chatRoomRepository.save(newRoom);
        return ChatRoomResponse.from(newRoom);
    }


    // 채팅방 입장
    @Transactional
    public void enterRoom(Long roomId, CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        Optional<ChatParticipant> existing = chatParticipantRepository.findByRoomIdAndUserId(roomId, userId);
        if (existing.isPresent()) {
            existing.get().reEnter(); // 재입장 처리
        } else {
            ChatParticipant participant = ChatParticipant.enter(room, user);
            room.addParticipant(participant);
        }
        room.increaseUserCount();
    }

    // 채팅방 나가기
    @Transactional
    public void exitRoom(Long roomId, CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        ChatParticipant participant = chatParticipantRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 참여자가 없습니다."));
        participant.exit();

        ChatRoom room = participant.getChatRoom();
        room.decreaseUserCount();

        if (room.isEmpty()) {
            chatRoomRepository.delete(room);
        }
    }
}
