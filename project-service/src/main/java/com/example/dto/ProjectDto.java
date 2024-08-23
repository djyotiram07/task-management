package com.example.dto;

import java.time.LocalDateTime;

public record ProjectDto(Long id,
                         String name,
                         String description,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt,
                         Long userId) {
}
