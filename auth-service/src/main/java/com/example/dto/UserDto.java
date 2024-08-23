package com.example.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserDto(Long id,
                      String username,
                      String email,
                      String password,
                      LocalDateTime createdAt,
                      LocalDateTime updatedAt,
                      Set<RoleDto> roles) {
}
