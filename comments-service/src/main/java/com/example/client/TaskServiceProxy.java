package com.example.client;

import com.example.config.FeignConfig;
import com.example.dto.TaskDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tm-task", configuration = FeignConfig.class)
public interface TaskServiceProxy {

    @GetMapping(value = "/api/v1/tm-task/{id}")
    TaskDto findTaskById(@PathVariable Long id);
}
