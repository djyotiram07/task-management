package com.example.config;

import com.example.filter.AuthenticationFilter;
import com.example.filter.CompositeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TmApiGatewayConfiguration {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    private CompositeFilter compositeFilter;

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {

        return builder.routes().
                route("auth-service",
                        r -> r.path("/api/v1/auth/**")
                                .filters(f -> f.filter(compositeFilter))
                                .uri("lb://auth-service")
                ).
                route("user-service",
                        r -> r.path("/api/v1/tm-user/**")
                                .filters(f -> f.filter(authenticationFilter))
                                .uri("lb://user-service")
                ).
                route("project-service",
                        r -> r.path("/api/v1/tm-project/**")
                                .filters(f -> f.filter(authenticationFilter))
                                .uri("lb://project-service")
                ).
                route("task-service",
                        r -> r.path("/api/v1/tm-task/**")
                                .filters(f -> f.filter(authenticationFilter))
                                .uri("lb://task-service")
                ).
                route("comment-service",
                        r -> r.path("/api/v1/tm-comment/**")
                                .filters(f -> f.filter(authenticationFilter))
                                .uri("lb://comment-service")
                ).
                route("notification-service",
                        r -> r.path("/ws/notification/**", "/api/v1/notification/**")
                                .filters(f -> f.filter(authenticationFilter))
                                .uri("lb://notification-service")
                ).
                route("analytics-service", r -> r.path("/api/v1/tm-analytics/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://analytics-service")
                ).
                route("kanban-service", r -> r.path("/api/v1/tm-kanban/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://kanban-service")
                ).
                route("search-service", r -> r.path("/api/v1/tm-search/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://search-service")
                ).
                build();
    }
}
