package com.example.controller;

import com.example.exceptions.AnalyticsCommonExceptions;
import com.example.utils.JwtService;
import lombok.RequiredArgsConstructor;
import com.example.dto.ProjectProgressDto;
import com.example.dto.TaskStatisticDto;
import com.example.dto.UserPerformanceDto;
import com.example.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/tm-analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final JwtService jwtService;

    @GetMapping("/task-statistics")
    public ResponseEntity<TaskStatisticDto> getTaskStatistics(@RequestHeader("Authorization") String token) {
        Set<String> roles = jwtService.extractRoles(token);
        if (!roles.contains("ROLE_ADMIN")) {
            throw new AnalyticsCommonExceptions("Access denied: Insufficient permissions to perform this operation.");
        }
        TaskStatisticDto taskStatisticDto = analyticsService.getTaskStatistic();
        return ResponseEntity.ok(taskStatisticDto);
    }

    @GetMapping("/user-performance/{userId}")
    public ResponseEntity<UserPerformanceDto> getUserPerformanceByUserId(@PathVariable Long userId) {
        UserPerformanceDto userPerformanceDto = analyticsService.getUserPerformanceByUserId(userId);
        return ResponseEntity.ok(userPerformanceDto);
    }

    @GetMapping("/project-progress/{projectId}")
    public ResponseEntity<ProjectProgressDto> getProjectProgressByProjectId(@PathVariable Long projectId) {
        ProjectProgressDto projectProgressDto = analyticsService.getProjectProgressByProjectId(projectId);
        return ResponseEntity.ok(projectProgressDto);
    }
}
