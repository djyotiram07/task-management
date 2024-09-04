package com.example.service;

import com.example.client.ProjectServiceProxy;
import com.example.client.UserServiceProxy;
import com.example.dto.ProjectDto;
import com.example.dto.TaskDto;
import com.example.dto.TaskStatusDto;
import com.example.dto.UserDto;
import com.example.event.ProjectDeleteEvent;
import com.example.event.TaskCreateEvent;
import com.example.event.TaskStatusEvent;
import com.example.event.UserDeleteEvent;
import com.example.exceptions.TaskCommonException;
import com.example.exceptions.TaskNotFoundException;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserServiceProxy userServiceProxy;
    private final ProjectServiceProxy projectServiceProxy;

    private final KafkaTemplate<String, TaskCreateEvent> kafkaTemplate1;
    private final KafkaTemplate<String, TaskStatusEvent> kafkaTemplate2;
    private final KafkaTemplate<String, Long> kafkaTemplate3;

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        log.info("Received create task request.");
        try {
            UserDto userDto = userServiceProxy.findUserById(taskDto.userId());
            ProjectDto projectDto = projectServiceProxy.findProjectById(taskDto.projectId());

            if (userDto == null || projectDto == null) {
                throw new TaskCommonException("User or Project not found.");
            }
            Task task = taskMapper.taskDtoToTask(taskDto);
            Task savedTask = taskRepository.save(task);
            sendTaskCreateEvent(savedTask, userDto.email());

            log.info("Task created successfully.");
            return taskMapper.taskToTaskDto(savedTask);
        } catch (Exception e) {
            throw new TaskCommonException("Error creating task: " + e.getMessage());
        }
    }

    @Override
    public TaskDto findTaskById(Long id) {
        log.info("Fetching task with ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
        log.info("Task found with ID: {}", id);
        return taskMapper.taskToTaskDto(task);
    }

    @Override
    public void updateTaskById(Long id, TaskDto taskDto) {
        log.info("Updating task with ID: {}", id);
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            throw new TaskNotFoundException("Task not found with id " + id);
        }

        try {
            Task updatedTask = taskMapper.taskDtoToTaskWithoutStatus(taskDto);
            updatedTask.setId(task.get().getId());
            updatedTask.setTaskStatus(task.get().getTaskStatus());
            taskRepository.save(updatedTask);
            log.info("Task updated successfully with ID: {}", id);
        } catch (Exception e) {
            throw new TaskCommonException("Error updating task: " + e.getMessage());
        }
    }

    @Override
    public void updateTaskStatusById(Long id, TaskStatusDto taskStatusDto) {
        log.info("Updating task status for task with ID: {}", id);
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            throw new TaskNotFoundException("Task not found with id " + id);
        }

        try {
            task.get().setTaskStatus(taskStatusDto.taskStatus());
            taskRepository.save(task.get());
            sendTaskStatusEvent(task.get());
            log.info("Task status updated successfully for task with ID: {}", id);
        } catch (Exception e) {
            throw new TaskCommonException("Error updating task: " + e.getMessage());
        }
    }

    @Override
    public void deleteTaskById(Long id) {
        log.info("Deleting task with ID: {}", id);
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id " + id);
        }

        try {
            taskRepository.deleteById(id);
            kafkaTemplate3.send("deleteTask", id);
            log.info("Task deleted successfully with ID: {}", id);
        } catch (Exception e) {
            throw new TaskCommonException("Error deleting task: " + e.getMessage());
        }
    }

    @Override
    public Page<TaskDto> findAllTasksByUserId(Long userId, Pageable pageable) {
        log.info("Fetching all tasks for user ID: {} with pageable: {}", userId, pageable);
        Page<Task> tasks = taskRepository.findAllByUserId(userId, pageable);
        log.info("Fetched {} tasks for user ID: {}", tasks.getTotalElements(), userId);
        return tasks.map(taskMapper::taskToTaskDto);
    }

    private void sendTaskCreateEvent(Task task, String username) {
        TaskCreateEvent taskCreateEvent =
                new TaskCreateEvent(task.getId(),
                        task.getUserId(), task.getDueDate(), task.getTitle(), username, task.getProjectId());
        kafkaTemplate1.send("createTask", taskCreateEvent);
    }

    private void sendTaskStatusEvent(Task task) {
        TaskStatusEvent taskStatusEvent =
                new TaskStatusEvent(task.getId(), task.getUserId(), task.getTaskStatus(),
                        task.getTitle(), task.getProjectId());
        kafkaTemplate2.send("updateTask", taskStatusEvent);
    }

    @KafkaListener(topics = "deleteProject")
    public void projectDeleteListener(ProjectDeleteEvent projectDeleteEvent) {
        log.info("Received delete message from project service: {}", projectDeleteEvent);

        if (!taskRepository.existsByProjectId(projectDeleteEvent.id())) {
            throw new TaskNotFoundException("Task not found with project id " + projectDeleteEvent.id());
        }

        try {
            Task task = taskRepository.findByProjectId(projectDeleteEvent.id());
            taskRepository.deleteByProjectId(projectDeleteEvent.id());
            kafkaTemplate3.send("deleteTask", task.getId());
        } catch (Exception e) {
            log.error("Failed to delete project with ID {}: {}", projectDeleteEvent.id(), e.getMessage());
        }
    }

    @KafkaListener(topics = "deleteUser")
    public void userDeleteListener(UserDeleteEvent userDeleteEvent) {
        log.info("Received delete message from user service: {}", userDeleteEvent);
        // another logic : just unassigned user
        if (!taskRepository.existsByUserId(userDeleteEvent.id())) {
            throw new TaskNotFoundException("Task not found with user id " + userDeleteEvent.id());
        }

        try {
            Task task = taskRepository.findByUserId(userDeleteEvent.id());
            taskRepository.deleteByUserId(userDeleteEvent.id());
        } catch (Exception e) {
            log.error("Failed to delete user with ID {}: {}", userDeleteEvent.id(), e.getMessage());
        }
    }
}
