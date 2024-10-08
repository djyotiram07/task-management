package com.example.service;

import com.example.client.UserServiceProxy;
import com.example.dto.ProjectDto;
import com.example.dto.UserDto;
import com.example.event.ProjectCreateEvent;
import com.example.exceptions.ProjectNotFoundException;
import com.example.mapper.ProjectMapper;
import com.example.model.Project;
import com.example.repository.ProjectRepository;
import com.example.exceptions.ProjectCommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserServiceProxy userServiceProxy;

    private final KafkaTemplate<String, ProjectCreateEvent> kafkaTemplate1;
    private final KafkaTemplate<String, Long> kafkaTemplate2;

    @Override
    public ProjectDto createProject(ProjectDto projectDto, String username) {
        log.info("Attempting to create project with details: {} by user: {}", projectDto, username);
        try {
            UserDto userDto = userServiceProxy.findUserById(projectDto.userId());
            if (userDto == null) {
                throw new ProjectCommonException(
                        "User not found: The requested user could not be found in the system.");
            } else if (!Objects.equals(userDto.email(), username)) {
                throw new ProjectCommonException(
                        "Authentication error: The email address associated with the provided username does not match.");
            }

            Project project = projectMapper.projectDtoToProject(projectDto);
            Project savedProject = projectRepository.save(project);

            log.info("Project created successfully.");
            return projectMapper.projectToProjectDto(savedProject);

        } catch (Exception e) {
            throw new ProjectCommonException("Error creating project: " + e.getMessage());
        }
    }

    @Override
    public ProjectDto findProjectById(Long id) {
        log.info("Fetching project with ID: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Project not found with ID: {}", id);
                    return new ProjectNotFoundException("Project not found with id " + id);
                });
        log.info("Project found with ID: {}", id);
        return projectMapper.projectToProjectDto(project);
    }

    @Override
    public void updateProjectById(Long id, ProjectDto projectDto) {
        log.info("Updating project with ID: {}", id);
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Project not found with id " + id);
        }

        try {
            Project project = projectMapper.projectDtoToProject(projectDto);
            project.setId(id);
            projectRepository.save(project);
            log.info("Project updated successfully with ID: {}", id);
        } catch (Exception e) {
            throw new ProjectCommonException("Error updating project: " + e.getMessage());
        }
    }

    @Override
    public void deleteProjectById(Long id) {
        log.info("Deleting project with ID: {}", id);
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Project not found with id " + id);
        }

        try {
            projectRepository.deleteById(id);
            kafkaTemplate2.send("deleteProject", id);
            log.info("Project deleted successfully with ID: {}", id);
        } catch (Exception e) {
            throw new ProjectCommonException("Error deleting project: " + e.getMessage());
        }
    }

    @Override
    public Page<ProjectDto> findAllProjectsByUserId(Long userId, Pageable pageable) {
        log.info("Fetching all projects for user ID: {} with pageable: {}", userId, pageable);
        Page<Project> projects = projectRepository.findAllByUserId(userId, pageable);

        log.info("Fetched {} projects for user ID: {}", projects.getTotalElements(), userId);
        return projects.map(projectMapper::projectToProjectDto);
    }

    private void sendProjectCreateEvent(Project project) {
        ProjectCreateEvent projectCreateEvent = new ProjectCreateEvent(project.getId(), project.getName());
        kafkaTemplate1.send("createProject", projectCreateEvent);
    }
}
