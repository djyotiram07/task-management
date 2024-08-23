package com.example.mapper;

import com.example.dto.TaskDto;
import com.example.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDto taskToTaskDto(Task task);

    @Mapping(target = "id", ignore = true)
    Task taskDtoToTask(TaskDto taskDto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "taskStatus", ignore = true)
    })
    Task taskDtoToTaskWithoutStatus(TaskDto taskDto);
}
