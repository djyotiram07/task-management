package com.example.service;

import com.example.event.TaskCreateEvent;
import com.example.event.TaskStatusEvent;
import com.example.utils.EmailContentBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${mail.from}")
    private String MAIL_FROM;

    @Value("${mail.subject[0]}")
    private String TASK_ASSIGNED_SUBJECT;

    @Value("${mail.subject[1]}")
    private String TASK_UPDATED_SUBJECT;

    @Value("${mail.subject[2]}")
    private String TASK_REMINDER_SUBJECT;

    private final JavaMailSender javaMailSender;
    private final EmailContentBuilder contentBuilder = new EmailContentBuilder();

    public void sendMailForCreate(TaskCreateEvent taskCreateEvent, String currentUser) {
        sendMail(currentUser,
                TASK_ASSIGNED_SUBJECT,
                contentBuilder.buildTaskCreationEmail(taskCreateEvent));
    }

    public void sendMailForUpdate(TaskStatusEvent taskStatusEvent, String currentUser) {
        sendMail(currentUser,
                TASK_UPDATED_SUBJECT,
                contentBuilder.buildTaskUpdateEmail(taskStatusEvent));
    }

    public void sendMailForReminder(String taskName, String currentUser) {
        sendMail(currentUser,
                TASK_REMINDER_SUBJECT,
                contentBuilder.buildTaskReminderEmail(taskName));
    }

    private void sendMail(String to, String subject, String text) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(MAIL_FROM);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(text, true);
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("Mail sent successfully to {}", to);
        } catch (MailException e) {
            log.error("Exception occurred while sending mail.", e);
            throw new RuntimeException("Exception occurred while sending mail from " + MAIL_FROM, e);
        }
    }
}

