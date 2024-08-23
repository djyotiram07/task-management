package com.example.event;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskCreateEvent {

    private Long id;
    private Long userId;
    private LocalDate dueDate;
    private String title;
    private String username;
    private Long projectId;
}
