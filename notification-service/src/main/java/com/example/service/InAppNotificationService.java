package com.example.service;

import com.example.event.TaskCreateEvent;
import com.example.event.TaskStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class InAppNotificationService {

    @Value("${mail.in-app-subject[0]}")
    private String  IN_APP_ASSIGNED_SUBJECT;

    @Value("${mail.in-app-subject[1]}")
    private String IN_APP_UPDATED_SUBJECT;

    @Value("${mail.in-app-subject[2]}")
    private String IN_APP_REMINDER_SUBJECT;

    private final ConcurrentHashMap<String, WebSocketSession> sessions;

    public void inAppNotificationForCreate(TaskCreateEvent taskCreateEvent, String currentUser) {
        sendNotification(currentUser,
                String.format(IN_APP_ASSIGNED_SUBJECT, taskCreateEvent.title()));
    }

    public void inAppNotificationForUpdate(TaskStatusEvent taskStatusEvent, String currentUser) {
        sendNotification(currentUser,
                String.format(IN_APP_UPDATED_SUBJECT, taskStatusEvent.title()));
    }

    public void inAppNotificationForReminder(String taskName, String currentUser) {
        sendNotification(currentUser,
                String.format(IN_APP_REMINDER_SUBJECT, taskName));
    }

    private void sendNotification(String currentUser, String message) {
        WebSocketSession session = sessions.get(currentUser);
        if (session == null || !session.isOpen()) {
            log.warn("User {} is not connected via WebSocket", currentUser);
            return;
        }

        try {
            session.sendMessage(new TextMessage(message));
            log.info("Notification sent to user {}: {}", currentUser, message);
        } catch (IOException e) {
            log.error("Error occurred while sending notification to user {}", currentUser, e);
            throw new RuntimeException("Error occurred while sending notification.", e);
        }
    }
}
