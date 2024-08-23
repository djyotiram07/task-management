package com.example.mapper;

import com.example.dto.TaskStatisticDto;
import com.example.model.TaskStatistics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskStatisticsMapper {

    TaskStatisticDto taskStatisticsToTaskStatisticsDto(TaskStatistics taskStatistics);

    @Mapping(target = "id", ignore = true)
    TaskStatistics taskStatisticsDtoToTaskStatistics(TaskStatisticDto taskStatisticDto);
}
