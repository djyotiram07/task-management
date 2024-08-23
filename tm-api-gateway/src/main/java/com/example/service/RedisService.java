package com.example.service;

import com.example.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final TimeUnit TOKEN_EXPIRATION = TimeUnit.HOURS;

    public void saveToken(String token) {
        redisTemplate.opsForValue().set(
                token,
                "active",
                1, TOKEN_EXPIRATION);
    }

    public boolean isTokenValid(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(token));
        } catch (Exception e) {
            throw new UnauthorizedException("Error occur while validating token with redis : " + e.getMessage());
        }
    }

    public void invalidateToken(String token) {
        redisTemplate.delete(token);
    }
}
