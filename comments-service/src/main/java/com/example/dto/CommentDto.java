package com.example.dto;

import java.time.LocalDateTime;

public record CommentDto(Long id,
                         String text,
                         Long taskId,
                         Long projectId,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt) {
}
