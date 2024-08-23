package com.example.mapper;

import com.example.dto.UserPerformanceDto;
import com.example.model.UserPerformance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPerformanceMapper {

    UserPerformanceDto userPerformanceToUserPerformanceDto(UserPerformance userPerformance);

    @Mapping(target = "id", ignore = true)
    UserPerformance userPerformanceDtoToUserPerformance(UserPerformanceDto userPerformanceDto);
}
