package com.example.event;

import java.time.LocalDate;

public record TaskCreateEvent(Long id,
                              Long userId,
                              LocalDate dueDate,
                              String title,
                              String username,
                              Long projectId) {
}
