package com.example.controller;

import com.example.dto.ProjectDto;
import com.example.exceptions.ProjectCommonException;
import com.example.service.ProjectService;
import com.example.utils.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/tm-project")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto projectDto,
                                                    UriComponentsBuilder ucb,
                                                    @RequestHeader("Authorization") String token) {
        log.info("Received create project request.");
        Set<String> roles = jwtService.extractRoles(token);
        if (!roles.contains("ROLE_ADMIN")) {
            throw new ProjectCommonException("Access denied: Insufficient permissions to perform this operation.");
        }

        String username = jwtService.extractUsername(token);
        ProjectDto createdProject = projectService.createProject(projectDto, username);
        URI location = ucb
                .path("/{id}")
                .buildAndExpand(createdProject.id())
                .toUri();

        log.info("Project created successfully.");
        return ResponseEntity.created(location).body(createdProject);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long projectId,
                                                     @RequestHeader("Authorization") String token) {
        // think
        Set<String> roles = jwtService.extractRoles(token);
        if (!roles.contains("ROLE_ADMIN")) {
            throw new ProjectCommonException("Access denied: Insufficient permissions to perform this operation.");
        }

        ProjectDto projectDto = projectService.findProjectById(projectId);
        return ResponseEntity.ok(projectDto);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Void> updateProject(@PathVariable Long projectId,
                                              @Valid @RequestBody ProjectDto projectDto,
                                              @RequestHeader("Authorization") String token) {
        Set<String> roles = jwtService.extractRoles(token);
        if (!roles.contains("ROLE_ADMIN")) {
            throw new ProjectCommonException("Access denied: Insufficient permissions to perform this operation.");
        }

        projectService.updateProjectById(projectId, projectDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId,
                                              @RequestHeader("Authorization") String token) {
        Set<String> roles = jwtService.extractRoles(token);
        if (!roles.contains("ROLE_ADMIN")) {
            throw new ProjectCommonException("Access denied: Insufficient permissions to perform this operation.");
        }

        projectService.deleteProjectById(projectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ProjectDto>> getAllProjectsByUserId(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProjectDto> projects =
                projectService.findAllProjectsByUserId(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(projects);
    }
}
