package com.example.service;

import com.example.dto.TaskDto;
import com.example.dto.TaskStatusDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskDto createTask(TaskDto taskDto);

    TaskDto findTaskById(Long id);

    void updateTaskById(Long id, TaskDto taskDto);

    void deleteTaskById(Long id);

    Page<TaskDto> findAllTasksByUserId(Long id, Pageable pageable);

    void updateTaskStatusById(Long id, TaskStatusDto taskStatusDto);
}
