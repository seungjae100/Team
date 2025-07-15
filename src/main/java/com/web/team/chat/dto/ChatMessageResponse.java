package com.web.team.chat.dto;

import com.web.team.chat.domain.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
@Schema(description = "채팅 메세지 응답 DTO")
public record ChatMessageResponse (

    @Schema(description = "채팅방 ID", example = "1 보안 리팩토링 예정")
    Long roomId,

    @Schema(description = "메세지 보낸 사람 ID", example = "1 보안 리팩토링 예정")
    Long senderId,

    @Schema(description = "메세지 보낸 사람 이름", example = "짱구")
    String senderName,

    @Schema(description = "메세지", example = "철수야 놀자")
    String message,

    @Schema(description = "메세지 보낸 시간", example = "14 : 00")
    LocalDateTime sentAt

)   {
    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getChatRoom().getId(),
                chatMessage.getSender().getId(),
                chatMessage.getSender().getName(),
                chatMessage.getMessage(),
                chatMessage.getSentAt()
        );
    }
}
