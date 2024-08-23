package com.example.controller;

import com.example.dto.NotificationDto;
import com.example.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<Page<NotificationDto>> getAllNotificationsByUserId(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<NotificationDto> projects =
                notificationService.getNotificationsByUserId(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(projects);
    }

    @PostMapping(value = "/{id}")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}
