package com.web.team.chat.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.team.chat.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(Long roomId, ChatMessageResponse response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.convertAndSend("chatroom:" + roomId, json);
        } catch (Exception e){
            throw new RuntimeException("Redis 메세지 발행 실패", e);
        }
    }
}
