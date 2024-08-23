package com.example.dto;

import com.example.utils.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskStatisticDto {

    private Long id;
    private Long taskId;
    private Long projectId;
    private Long userId;
    private TaskStatus taskStatus;
    private Duration timeToComplete;
    private LocalDateTime completionDate;
    private LocalDateTime createdAt;
}
