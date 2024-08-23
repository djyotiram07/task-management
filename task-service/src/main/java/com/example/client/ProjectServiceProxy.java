package com.example.client;

import com.example.config.FeignConfig;
import com.example.dto.ProjectDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "project-service", configuration = FeignConfig.class)
public interface ProjectServiceProxy {

    @GetMapping(value = "/api/v1/tm-project/{id}")
    ProjectDto findProjectById(@PathVariable Long id);
}
