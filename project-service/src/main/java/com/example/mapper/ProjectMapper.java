package com.example.mapper;

import com.example.dto.ProjectDto;
import com.example.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectDto projectToProjectDto(Project project);

    @Mapping(target = "id", ignore = true)
    Project projectDtoToProject(ProjectDto projectDto);
}
