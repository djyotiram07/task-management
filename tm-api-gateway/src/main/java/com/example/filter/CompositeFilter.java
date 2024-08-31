package com.example.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CompositeFilter implements GatewayFilter {

    @Autowired
    private AuthenticationFilter authenticationFilter;
    private static final String LOGOUT_PATH = "/api/v1/auth/logout";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        System.out.println("path : "+path);
        if (LOGOUT_PATH.equals(path)) {
            return authenticationFilter.filter(exchange, chain);
        }
        return chain.filter(exchange);
    }
}