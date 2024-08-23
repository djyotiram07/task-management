package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ExecutionTracker {

    private static final String EXECUTION_KEY = "methodExecuted";
    private static final TimeUnit EXECUTION_TTL = TimeUnit.HOURS;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean hasMethodExecuted() {
        String value = redisTemplate.opsForValue().get(EXECUTION_KEY);
        return "true".equals(value);

        //            boolean isTokenValid = redisService.isTokenValid(token);
//            System.out.println("is token valid : "+isTokenValid);
//            if (isTokenValid) {
//                throw new UnauthorizedException("Invalid or missing authorization token.");
//            }
//
//            if (LOGOUT_PATH.equals(path)) {
//                redisService.invalidateToken(token);
//            } else if (!executionTracker.hasMethodExecuted()) {
//                System.out.println("-------------------------------------------------");
//                System.out.println("save token to redis : "+path);
//                System.out.println("-------------------------------------------------");
//                redisService.saveToken(token);
//                executionTracker.markMethodExecuted();
//            }
    }

    public void markMethodExecuted() {
        redisTemplate.opsForValue().set(EXECUTION_KEY, "true", 1, EXECUTION_TTL);
    }
}
