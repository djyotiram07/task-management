package com.example.dto;

import java.time.Duration;
import java.time.LocalDateTime;

public class UserPerformanceDto {

    private Long id;
    private Long userId;
    private int tasksCompleted;
    private int tasksAssigned;
    private Duration averageCompletionTime;
    private double performanceScore;
    private LocalDateTime lastUpdatedAt;
}
