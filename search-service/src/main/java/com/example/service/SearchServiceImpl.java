package com.example.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.event.*;
import com.example.exceptions.SearchCommonException;
import com.example.model.IndexedProject;
import com.example.model.IndexedTask;
import com.example.model.IndexedUser;
import com.example.repository.IndexedProjectRepository;
import com.example.repository.IndexedTaskRepository;
import com.example.repository.IndexedUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private final IndexedTaskRepository taskRepository;
    private final IndexedProjectRepository projectRepository;
    private final IndexedUserRepository userRepository;

    @Override
    public Page<IndexedTask> searchTasks(String query, int page, int size) {
        log.info("Searching for tasks with query: {}", query);
        PageRequest pageRequest = PageRequest.of(page, size);
        return taskRepository.findByTitleFuzzy(query, pageRequest);
    }

    @Override
    public Page<IndexedProject> searchProjects(String query, int page, int size) {
        log.info("Searching for projects with query: {}", query);
        PageRequest pageRequest = PageRequest.of(page, size);
        return projectRepository.findByTitleFuzzy(query, pageRequest);
    }

    @Override
    public Page<IndexedUser> searchUsers(String query, int page, int size) {
        log.info("Searching for users with query: {}", query);
        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findByUsernameFuzzy(query, pageRequest);
    }

    @KafkaListener(topics = "createTask")
    public void taskCreateListener(TaskCreateEvent taskCreateEvent) {
        log.info("Received create message from task service: {}", taskCreateEvent);

        try {
            IndexedTask indexedTask = IndexedTask.builder()
                    .taskId(taskCreateEvent.id())
                    .title(taskCreateEvent.title())
                    .build();
            taskRepository.save(indexedTask);
        } catch (Exception e) {
            throw new SearchCommonException("Failed to process task create event." + e.getMessage());
        }
    }

    @KafkaListener(topics = "createProject")
    public void projectCreateListener(ProjectCreateEvent projectCreateEvent) {
        log.info("Received create message from project service: {}", projectCreateEvent);

        try {
            IndexedProject indexedProject = IndexedProject.builder()
                    .projectId(projectCreateEvent.projectId())
                    .title(projectCreateEvent.title())
                    .build();
            projectRepository.save(indexedProject);
        } catch (Exception e) {
            throw new SearchCommonException("Failed to process project create event." + e.getMessage());
        }
    }

    @KafkaListener(topics = "createUser")
    public void userCreateListener(UserCreateEvent userCreateEvent) {
        log.info("Received create message from user service: {}", userCreateEvent);

        try {
            IndexedUser indexedUser = IndexedUser.builder()
                    .userId(userCreateEvent.userId())
                    .username(userCreateEvent.username())
                    .build();
            userRepository.save(indexedUser);
        } catch (Exception e) {
            throw new SearchCommonException("Failed to process user create event." + e.getMessage());
        }
    }

    @KafkaListener(topics = "deleteTask")
    public void taskDeleteListener(TaskDeleteEvent taskDeleteEvent) {
        log.info("Received delete message from task service: {}", taskDeleteEvent);

        try {
            taskRepository.deleteByTaskId(taskDeleteEvent.id());
        } catch (Exception e) {
            log.error("Failed to delete task with ID {}: {}", taskDeleteEvent.id(), e.getMessage());
        }
    }

    @KafkaListener(topics = "deleteProject")
    public void projectDeleteListener(ProjectDeleteEvent projectDeleteEvent) {
        log.info("Received delete message from project service: {}", projectDeleteEvent);

        try {
            projectRepository.deleteByProjectId(projectDeleteEvent.id());
        } catch (Exception e) {
            log.error("Failed to delete project with ID {}: {}", projectDeleteEvent.id(), e.getMessage());
        }
    }

    @KafkaListener(topics = "deleteUser")
    public void userDeleteListener(UserDeleteEvent userDeleteEvent) {
        log.info("Received delete message from user service: {}", userDeleteEvent);

        try {
            userRepository.deleteByUserId(userDeleteEvent.id());
        } catch (Exception e) {
            log.error("Failed to delete user with ID {}: {}", userDeleteEvent.id(), e.getMessage());
        }
    }
}
