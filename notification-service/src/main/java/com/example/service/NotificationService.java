package com.example.service;

import com.example.dto.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void sendNotification(NotificationDto notificationDto);

    Page<NotificationDto> getNotificationsByUserId(Long userId, Pageable pageable);

    void markAsRead(Long notificationId);
}
