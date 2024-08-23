package com.example.filter;

import com.example.exceptions.UnauthorizedException;
import com.example.service.ExecutionTracker;
import com.example.service.RedisService;
import com.example.utils.JwtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GatewayFilter {

    private static final Logger log = LogManager.getLogger(AuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String LOGOUT_PATH = "/api/v1/auth/logout";

    @Autowired
    private RouteValidator routeValidator;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RedisService redisService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        log.info("Authentication for path: {}", path);

        if (routeValidator.isSecured.test(request)) {
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                throw new UnauthorizedException("Missing or invalid authorization header.");
            }

            String token = authHeader.substring(BEARER_PREFIX.length());

            try {
                jwtService.validateToken(token);
            } catch (Exception e) {
                log.error("Invalid access.");
                throw new UnauthorizedException("Unauthorized access to application");
            }

            if (redisService.isTokenValid(token)) {
                throw new UnauthorizedException("Invalid or missing authorization token.");
            }

            if (LOGOUT_PATH.equals(path)) {
                if (!redisService.isTokenValid(token)) {
                    redisService.saveToken(token);
                }
            }
        }

        return chain.filter(exchange);
    }
}

