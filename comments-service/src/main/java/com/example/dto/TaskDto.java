package com.example.dto;

import java.time.LocalDate;

public record TaskDto(Long id,
                      String title,
                      String description,
                      LocalDate dueDate,
                      TaskPriority taskPriority,
                      TaskStatus taskStatus,
                      Long userId,
                      Long projectId) {
}
