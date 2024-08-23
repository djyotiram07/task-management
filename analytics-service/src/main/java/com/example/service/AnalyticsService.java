package com.example.service;

import com.example.dto.ProjectProgressDto;
import com.example.dto.TaskStatisticDto;
import com.example.dto.UserPerformanceDto;

public interface AnalyticsService {

    TaskStatisticDto getTaskStatistic();

    UserPerformanceDto getUserPerformanceByUserId(Long userId);

    ProjectProgressDto getProjectProgressByProjectId(Long projectId);
}
