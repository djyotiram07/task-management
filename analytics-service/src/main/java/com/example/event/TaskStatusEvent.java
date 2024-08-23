package com.example.event;

import com.example.utils.TaskStatus;
import lombok.Data;

@Data
public class TaskStatusEvent {

    private Long id;
    private Long userId;
    private TaskStatus taskStatus;
    private String title;
    private Long projectId;
}
