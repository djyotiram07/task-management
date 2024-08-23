package com.example.service;

import com.example.dto.ProjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

    ProjectDto createProject(ProjectDto projectDto, String username);

    ProjectDto findProjectById(Long id);

    void updateProjectById(Long id, ProjectDto projectDto);

    void deleteProjectById(Long id);

    Page<ProjectDto> findAllProjectsByUserId(Long id, Pageable pageable);
}
