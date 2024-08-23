package com.example.controller;

import com.example.dto.TaskDto;
import com.example.dto.TaskStatusDto;
import com.example.exceptions.TaskCommonException;
import com.example.service.TaskService;
import com.example.utils.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/tm-task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto,
                                              UriComponentsBuilder ucb,
                                              @RequestHeader("Authorization") String token) {
        Set<String> roles = jwtService.extractRoles(token);
        if (!roles.contains("ROLE_ADMIN")) {
            throw new TaskCommonException("Access denied: Insufficient permissions to perform this operation.");
        }

        TaskDto createdTask = taskService.createTask(taskDto);
        URI location = ucb
                .path("/{id}")
                .buildAndExpand(createdTask.id())
                .toUri();

        return ResponseEntity.created(location).body(createdTask);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long taskId) {

        TaskDto taskDto = taskService.findTaskById(taskId);
        return ResponseEntity.ok(taskDto);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Void> updateTask(@PathVariable Long taskId,
                                           @Valid @RequestBody TaskDto taskDto) {

        taskService.updateTaskById(taskId, taskDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/status/{taskId}")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long taskId,
                                                 @Valid @RequestBody TaskStatusDto taskStatusDto) {

        taskService.updateTaskStatusById(taskId, taskStatusDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId,
                                           @RequestHeader("Authorization") String token) {
        Set<String> roles = jwtService.extractRoles(token);
        if (!roles.contains("ROLE_ADMIN")) {
            throw new TaskCommonException("Access denied: Insufficient permissions to perform this operation.");
        }

        taskService.deleteTaskById(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<TaskDto>> getAllTasksByUserId(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TaskDto> tasks =
                taskService.findAllTasksByUserId(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(tasks);
    }
}

