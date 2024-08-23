package com.example.event;

public record ProjectCreateEvent(Long projectId,
                                 String title) {
}
