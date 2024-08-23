package com.example.client;

import com.example.config.FeignConfig;
import com.example.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserServiceProxy {

    @GetMapping(value = "/api/v1/tm-user/{id}")
    UserDto findUserById(@PathVariable Long id);
}
