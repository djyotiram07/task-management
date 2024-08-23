package com.example.service;

import com.example.dto.NotificationDto;
import com.example.event.TaskCreateEvent;
import com.example.event.TaskStatusEvent;
import com.example.event.UserDeleteEvent;
import com.example.exceptions.NotificationCommonException;
import com.example.mapper.NotificationMapper;
import com.example.model.Notification;
import com.example.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Value("${mail.subject[0]}")
    private String TASK_ASSIGNED_SUBJECT;
    @Value("${mail.subject[1]}")
    private String TASK_UPDATED_SUBJECT;
    @Value("${mail.subject[2]}")
    private String TASK_REMINDER_SUBJECT;

    private final ExecutorService executorService;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;
    private final InAppNotificationService inAppNotificationService;

    public void sendNotification(NotificationDto notificationDto) {
        Notification notification = notificationMapper.notificationDtoToNotification(notificationDto);
        notificationRepository.save(notification);
    }

    public void updateNotification(Notification notification) {
        notification.getMessages().add(TASK_UPDATED_SUBJECT);
        notificationRepository.save(notification);
    }

    public Page<NotificationDto> getNotificationsByUserId(Long userId, Pageable pageable) {
        Page<Notification> projects = notificationRepository.findAllByUserId(userId, pageable);
        return projects.map(notificationMapper::notificationToNotificationDto);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationCommonException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @KafkaListener(topics = "createTask")
    public void taskCreateListener(TaskCreateEvent taskCreateEvent) {
        log.info("Got create message from task service {} : ", taskCreateEvent);

        try {
            NotificationDto notificationDto = new NotificationDto(null,
                    List.of(TASK_ASSIGNED_SUBJECT),
                    false, null, taskCreateEvent.dueDate(),
                    taskCreateEvent.username(), taskCreateEvent.title(), taskCreateEvent.userId());
            sendNotification(notificationDto);
            emailService.sendMailForCreate(taskCreateEvent, taskCreateEvent.username());
            inAppNotificationService.inAppNotificationForCreate(taskCreateEvent, taskCreateEvent.username());
        } catch (Exception e) {
            throw new NotificationCommonException("Error occurred during getting user." + e.getMessage());
        }
    }

    @KafkaListener(topics = "updateTask")
    public void taskStatusUpdateListener(TaskStatusEvent taskStatusEvent) {
        log.info("Got update message from task service {} : ", taskStatusEvent);
        Optional<Notification> notification = notificationRepository.findByUserId(taskStatusEvent.userId());
        String currentUser = notification.map(Notification::getEmail).orElse(null);

        if (currentUser != null) {
            updateNotification(notification.get());
            emailService.sendMailForUpdate(taskStatusEvent, currentUser);
            inAppNotificationService.inAppNotificationForUpdate(taskStatusEvent, currentUser);
        } else {
            throw new NotificationCommonException("Error occurred during getting user.");
        }
    }

    @Scheduled(cron = "${mail.cron}")
    private void taskReminderMail() {
        log.info("Inside Task reminder mail.");

        List<Notification> notifications = notificationRepository.findAll();

        notifications.forEach(recipient -> CompletableFuture.runAsync(() -> {
            try {
                long remaining = ChronoUnit.DAYS.between(LocalDate.now(), recipient.getDueDate());
                log.info("User : {} - Remaining days : {}", recipient.getEmail(), remaining);

                if (remaining == 1) {
                    emailService.sendMailForReminder(recipient.getTaskName(), recipient.getEmail());
                    inAppNotificationService.inAppNotificationForReminder(recipient.getTaskName(), recipient.getEmail());
                }
            } catch (Exception e) {
                log.error("Error while sending reminder mail to recipient: {}", recipient.getEmail(), e);
            }
        }, executorService));
    }

    @KafkaListener(topics = "deleteUser")
    public void userDeleteListener(UserDeleteEvent userDeleteEvent) {
        log.info("Received delete message from user service: {}", userDeleteEvent);

        try {
            notificationRepository.deleteByUserId(userDeleteEvent.id());
        } catch (Exception e) {
            log.error("Failed to delete user with ID {}: {}", userDeleteEvent.id(), e.getMessage());
        }
    }
}
