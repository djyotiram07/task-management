package com.example.event;

import com.example.model.Role;

import java.time.LocalDateTime;
import java.util.Set;

public record UserRegisterEvent(Long id,
                                String username,
                                String email,
                                String password,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt,
                                Set<Role> roles) {
}
