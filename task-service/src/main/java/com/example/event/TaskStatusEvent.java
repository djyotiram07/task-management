package com.example.event;

import com.example.utils.TaskStatus;

public record TaskStatusEvent(Long id,
                              Long userId,
                              TaskStatus taskStatus,
                              String title,
                              Long projectId) {
}
