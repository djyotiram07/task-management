package com.example.event;

import com.example.utils.TaskStatus;

public record TaskStatusEvent(Long id,
                              Long userId,
                              TaskStatus taskStatus,
                              String title,
                              Long projectId) {

    @Override
    public String toString() {
        return String.format("Task ID: %s%nAssigned To: %s%nTask Status: %s%nTask Title: %s",
                id, userId, taskStatus, title);
    }
}
