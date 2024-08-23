package com.example.config;

import com.example.exceptions.NotificationCommonException;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String authorizationHeader = getJwtFromRequest();
            if (authorizationHeader != null) {
                requestTemplate.header("Authorization", authorizationHeader);
            } else {
                throw new NotificationCommonException("Missing appropriate headers.");
            }
        };
    }

    private String getJwtFromRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = (HttpServletRequest) requestAttributes
                    .resolveReference(RequestAttributes.REFERENCE_REQUEST);

            if (request != null) {
                return request.getHeader("Authorization");
            }
        }
        return null;
    }
}
