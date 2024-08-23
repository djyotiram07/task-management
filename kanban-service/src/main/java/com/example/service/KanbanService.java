package com.example.service;

import com.example.model.KanbanBoard;
import com.example.model.TaskStatuses;
import com.example.utils.TaskStatus;

public interface KanbanService {

    KanbanBoard getKanbanBoardByProjectId(Long projectId);

    TaskStatuses updateTaskStatus(Long taskStatusId, TaskStatus taskStatus);
}
