package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.repository")
@EnableJpaAuditing
@EnableScheduling
public class AppConfig {

    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }
}
