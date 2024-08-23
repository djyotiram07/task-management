package com.example.controller;

import com.example.model.KanbanBoard;
import com.example.model.TaskStatuses;
import com.example.service.KanbanService;
import com.example.utils.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/tm-kanban")
@RequiredArgsConstructor
public class KanbanController {

    private final KanbanService kanbanService;

    @GetMapping("/board/{projectId}")
    public ResponseEntity<KanbanBoard> getKanbanBoardByProjectId(@PathVariable Long projectId) {
        KanbanBoard kanbanBoard = kanbanService.getKanbanBoardByProjectId(projectId);
        return ResponseEntity.ok(kanbanBoard);
    }

    @PutMapping("/task-status/{taskStatusId}")
    public ResponseEntity<TaskStatuses> updateTaskStatus(@PathVariable Long taskStatusId,
                                                         @RequestBody TaskStatus taskStatus) {
        TaskStatuses updatedTaskStatus = kanbanService.updateTaskStatus(taskStatusId, taskStatus);
        return ResponseEntity.ok(updatedTaskStatus);
    }
}
