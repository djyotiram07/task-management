package com.example.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class CustomWebSocketHandler extends AbstractWebSocketHandler {

//    private final RedisTemplate<String, WebSocketSession> redisTemplate;

    private final ConcurrentHashMap<String, WebSocketSession> sessions;

    @Value("${session.key.prefix}")
    private String SESSION_KEY_PREFIX;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        var principal = session.getAttributes().get("principal");
        if (!(principal instanceof String) || ((String) principal).isEmpty()) {
            session.close(CloseStatus.SERVER_ERROR.withReason("User must be authenticated"));
            return;
        }

        sessions.put((String) principal, session);
//        redisTemplate.opsForValue().set(SESSION_KEY_PREFIX + principal, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        var principal = session.getAttributes().get("principal");
        if (principal instanceof String) {
//            redisTemplate.delete(SESSION_KEY_PREFIX + principal);
            sessions.remove(principal);
        }
    }
}
