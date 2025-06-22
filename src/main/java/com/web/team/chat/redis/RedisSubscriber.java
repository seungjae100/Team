package com.web.team.chat.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.team.chat.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            ChatMessageResponse response = objectMapper.readValue(json, ChatMessageResponse.class);

            Long roomId = response.getRoomId();
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, response);
        } catch (Exception e) {
            throw new RuntimeException("Redis 메세지 구독 실패", e);
        }
    }
}
