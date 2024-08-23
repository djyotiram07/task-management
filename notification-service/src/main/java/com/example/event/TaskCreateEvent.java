package com.example.event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record TaskCreateEvent(Long id,
                              Long userId,
                              LocalDate dueDate,
                              String title,
                              String username,
                              Long projectId) {

    @Override
    public String toString() {
        return String.format("Task ID: %s%nAssigned To: %s%nDeadline: %s%nTask Title: %s",
                id, userId, dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), title);
    }
}

