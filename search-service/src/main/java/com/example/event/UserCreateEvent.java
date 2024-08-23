package com.example.event;

public record UserCreateEvent(Long userId,
                              String username) {
}
