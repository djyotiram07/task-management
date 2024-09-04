package com.example.service;

import com.example.event.ProjectDeleteEvent;
import com.example.event.UserDeleteEvent;
import com.example.exceptions.ProjectProgressNotFound;
import com.example.exceptions.UserPerformanceNotFound;
import com.example.repository.ProjectProgressRepository;
import com.example.repository.TaskStatisticsRepository;
import lombok.RequiredArgsConstructor;
import com.example.dto.ProjectProgressDto;
import com.example.dto.TaskStatisticDto;
import com.example.dto.UserPerformanceDto;
import com.example.event.TaskCreateEvent;
import com.example.event.TaskStatusEvent;
import com.example.exceptions.TaskStatisticsNotFound;
import com.example.mapper.ProjectProgressMapper;
import com.example.mapper.TaskStatisticsMapper;
import com.example.mapper.UserPerformanceMapper;
import com.example.model.AnalyticsData;
import com.example.model.ProjectProgress;
import com.example.model.TaskStatistics;
import com.example.model.UserPerformance;
import com.example.repository.AnalyticsDataRepository;
import com.example.repository.UserPerformanceRepository;
import com.example.utils.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserPerformanceRepository userPerformanceRepository;
    private final ProjectProgressRepository projectProgressRepository;
    private final TaskStatisticsRepository taskStatisticsRepository;
    private final AnalyticsDataRepository analyticsDataRepository;

    private final TaskStatisticsMapper taskStatisticsMapper;
    private final UserPerformanceMapper userPerformanceMapper;
    private final ProjectProgressMapper projectProgressMapper;

    @Override
    public TaskStatisticDto getTaskStatistic() {
        log.info("Fetching the latest task statistics.");
        TaskStatistics taskStatistics = taskStatisticsRepository.findLatestTaskStatistics()
                .orElseThrow(() -> {
                    log.error("Task statistics not found.");
                    return new TaskStatisticsNotFound("Task Statistics Not Found");
                });

        return taskStatisticsMapper.taskStatisticsToTaskStatisticsDto(taskStatistics);
    }

    @Override
    public UserPerformanceDto getUserPerformanceByUserId(Long userId) {
        log.info("Fetching user performance for userId: {}", userId);
        UserPerformance userPerformance = userPerformanceRepository.findLatestByUserId(userId)
                .orElseThrow(() -> {
                    log.error("User performance not found for userId: {}", userId);
                    return new UserPerformanceNotFound("UserPerformance not found for userId: " + userId);
                });

        return userPerformanceMapper.userPerformanceToUserPerformanceDto(userPerformance);
    }

    @Override
    public ProjectProgressDto getProjectProgressByProjectId(Long projectId) {
        log.info("Fetching project progress for projectId: {}", projectId);
        ProjectProgress projectProgress = projectProgressRepository.findLatestByProjectId(projectId)
                .orElseThrow(() -> {
                    log.error("Project progress not found for projectId: {}", projectId);
                    return new ProjectProgressNotFound("ProjectProgress not found for projectId: " + projectId);
                });

        return projectProgressMapper.projectProgressToProjectProgressDto(projectProgress);
    }

    @KafkaListener(topics = "createTask")
    public void taskCreateEventListener(TaskCreateEvent taskCreateEvent) {
        AnalyticsData analyticsData = AnalyticsData.builder()
                .taskId(taskCreateEvent.getId())
                .projectId(taskCreateEvent.getProjectId())
                .userId(taskCreateEvent.getUserId())
                .status(TaskStatus.TO_DO)
                .completionDate(taskCreateEvent.getDueDate().atStartOfDay())
                .build();

        analyticsDataRepository.save(analyticsData);
    }

    @KafkaListener(topics = "updateTask")
    public void taskStatusUpdateEventListener(TaskStatusEvent taskStatusEvent) {
        log.info("Received task status update event for taskId: {}", taskStatusEvent.getId());
        AnalyticsData analyticsData = analyticsDataRepository.findByTaskId(taskStatusEvent.getId())
                .orElseThrow(() -> {
                    log.error("Analytics data not found for taskId: {}", taskStatusEvent.getId());
                    return new RuntimeException("Data not found");
                });

        analyticsData.setStatus(taskStatusEvent.getTaskStatus());
        if (taskStatusEvent.getTaskStatus() == TaskStatus.DONE) {
            LocalDateTime now = LocalDateTime.now();
            analyticsData.setCompletionDate(now);

            long timeToComplete = calculateTimeToComplete(analyticsData.getCreatedAt(), now);
            analyticsData.setTimeToComplete(timeToComplete);
        }

        analyticsDataRepository.save(analyticsData);
    }

    @KafkaListener(topics = "deleteProject")
    public void projectDeleteListener(ProjectDeleteEvent projectDeleteEvent) {
        log.info("Received delete message from project service: {}", projectDeleteEvent);

        try {
            projectProgressRepository.deleteByProjectId(projectDeleteEvent.getId());
        } catch (Exception e) {
            log.error("Failed to delete project with ID {}: {}", projectDeleteEvent.getId(), e.getMessage());
        }
    }

    @KafkaListener(topics = "deleteUser")
    public void userDeleteListener(UserDeleteEvent userDeleteEvent) {
        log.info("Received delete message from user service: {}", userDeleteEvent);

        try {
            userPerformanceRepository.deleteByUserId(userDeleteEvent.getId());
        } catch (Exception e) {
            log.error("Failed to delete user with ID {}: {}", userDeleteEvent.getId(), e.getMessage());
        }
    }

    private long calculateTimeToComplete(LocalDateTime creationDate, LocalDateTime completionDate) {
        return Duration.between(creationDate, completionDate).toMillis();
    }

}
