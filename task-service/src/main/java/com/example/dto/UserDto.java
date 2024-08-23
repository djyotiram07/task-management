package com.example.dto;


import java.time.LocalDateTime;

public record UserDto(Long id,
                      String username,
                      String email,
                      String password,
                      LocalDateTime createdAt,
                      LocalDateTime updatedAt) {
}
