package com.example.service;

import com.example.event.ProjectDeleteEvent;
import com.example.event.TaskCreateEvent;
import com.example.event.TaskDeleteEvent;
import com.example.exceptions.KanbanBoardNotFoundException;
import com.example.exceptions.KanbanCommonException;
import com.example.model.KanbanBoard;
import com.example.model.TaskStatuses;
import com.example.repository.KanbanBoardRepository;
import com.example.repository.TaskStatusRepository;
import com.example.utils.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KanbanServiceImpl implements KanbanService {

    private final KanbanBoardRepository kanbanBoardRepository;
    private final TaskStatusRepository taskStatusRepository;

    @Override
    public KanbanBoard getKanbanBoardByProjectId(Long projectId) {
        KanbanBoard kanbanBoard = kanbanBoardRepository.findByProjectId(projectId);

        if (kanbanBoard != null) {
            return kanbanBoard;
        } else {
            throw new KanbanBoardNotFoundException("KanbanBoard not found for project with id : " + projectId);
        }
    }

    @Override
    public TaskStatuses updateTaskStatus(Long taskStatusId, TaskStatus taskStatus) {
        Optional<TaskStatuses> optionalTaskStatus = taskStatusRepository.findById(taskStatusId);
        if (optionalTaskStatus.isPresent()) {
            TaskStatuses newTaskStatus = optionalTaskStatus.get();
            newTaskStatus.setStatusName(taskStatus);
            return taskStatusRepository.save(newTaskStatus);
        }
        throw new KanbanCommonException("TaskStatus not found");
    }

    @KafkaListener(topics = "createTask")
    public void taskCreateListener(TaskCreateEvent taskCreateEvent) {
        log.info("Received create message from task service: {}", taskCreateEvent);

        TaskStatuses taskStatus = TaskStatuses.builder()
                .taskId(taskCreateEvent.id())
                .taskTitle(taskCreateEvent.title())
                .statusName(TaskStatus.TO_DO)
                .build();

        KanbanBoard kanbanBoard = kanbanBoardRepository.findByProjectId(taskCreateEvent.projectId());
        if (kanbanBoard == null) {
            kanbanBoard = KanbanBoard.builder()
                    .projectId(taskCreateEvent.projectId())
                    .taskStatuses(List.of(taskStatus))
                    .build();
        } else {
            kanbanBoard.getTaskStatuses().add(taskStatus);
        }
        taskStatus.setKanbanBoard(kanbanBoard);

        kanbanBoardRepository.save(kanbanBoard);
        taskStatusRepository.save(taskStatus);

        log.info("Kanban board and task status saved successfully.");
    }

    @KafkaListener(topics = "deleteTask")
    public void taskDeleteListener(TaskDeleteEvent taskDeleteEvent) {
        log.info("Received delete message from task service: {}", taskDeleteEvent);

        try {
            taskStatusRepository.deleteByTaskId(taskDeleteEvent.id());
        } catch (Exception e) {
            log.error("Failed to delete task with ID {}: {}", taskDeleteEvent.id(), e.getMessage());
        }
    }

    @KafkaListener(topics = "deleteProject")
    public void projectDeleteListener(ProjectDeleteEvent projectDeleteEvent) {
        log.info("Received delete message from project service: {}", projectDeleteEvent);

        try {
            kanbanBoardRepository.deleteByProjectId(projectDeleteEvent.id());
        } catch (Exception e) {
            log.error("Failed to delete project with ID {}: {}", projectDeleteEvent.id(), e.getMessage());
        }
    }
}