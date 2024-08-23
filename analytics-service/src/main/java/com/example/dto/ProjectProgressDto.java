package com.example.dto;

import java.time.LocalDateTime;

public class ProjectProgressDto {

    private Long id;
    private Long projectId;
    private int tasksCompleted;
    private int totalTasks;
    private double completionPercentage;
    private LocalDateTime estimatedCompletionDate;
    private LocalDateTime lastUpdatedAt;
}
