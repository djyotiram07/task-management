package com.example.mapper;

import com.example.dto.ProjectProgressDto;
import com.example.model.ProjectProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectProgressMapper {

    ProjectProgressDto projectProgressToProjectProgressDto(ProjectProgress projectProgress);

    @Mapping(target = "id", ignore = true)
    ProjectProgress projectProgressDtoToProjectProgress(ProjectProgressDto projectProgress);
}
