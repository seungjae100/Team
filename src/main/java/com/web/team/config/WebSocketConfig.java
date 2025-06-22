package com.web.team.config;

import com.web.team.jwt.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메세지를 구독하는 클라이언트 주소 prefix -> 메세지를 보내는 쪽이 아님
        config.enableSimpleBroker("/topic"); // 구독 경로
        config.setApplicationDestinationPrefixes("/app"); // 메세지 발행 경로
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹소켓 핸드쉐이크 연결을 위한 엔드포인트 정의
        registry.addEndpoint("/ws/chat") // 클라이언트 연결 경로
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns("*") // CORS 허용
                .withSockJS(); // SockJS 지원

    }
}
