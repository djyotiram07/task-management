package com.example.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record NotificationDto(Long id,
                              List<String> messages,
                              boolean isRead,
                              LocalDateTime createdAt,
                              LocalDate dueDate,
                              String email,
                              String taskName,
                              Long userId) {
}
