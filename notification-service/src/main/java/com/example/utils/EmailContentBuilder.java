package com.example.utils;

import com.example.event.TaskCreateEvent;
import com.example.event.TaskStatusEvent;

public class EmailContentBuilder {

    public String buildTaskCreationEmail(TaskCreateEvent taskCreateEvent) {
        return String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2 style="color: #2E86C1;">New Task Assigned</h2>
                    <p>Hi,</p>
                    <p>You have been assigned a new task:</p>
                    <p style="background-color: #f9f9f9; padding: 10px; border-left: 4px solid #2E86C1;">
                        %s
                    </p>
                    <p>Best Regards,</p>
                    <p><strong>Task Manager</strong></p>
                </body>
                </html>
                """, taskCreateEvent.toString());
    }

    public String buildTaskUpdateEmail(TaskStatusEvent taskStatusEvent) {
        return String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2 style="color: #2E86C1;">Task Updated</h2>
                    <p>Hi,</p>
                    <p>Your task has been updated:</p>
                    <p style="background-color: #f9f9f9; padding: 10px; border-left: 4px solid #2E86C1;">
                        %s
                    </p>
                    <p>Best Regards,</p>
                    <p><strong>Task Manager</strong></p>
                </body>
                </html>
                """, taskStatusEvent.toString());
    }

    public String buildTaskReminderEmail(String taskName) {
        return String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2 style="color: #E74C3C;">Task Deadline Reminder</h2>
                    <p>Hi,</p>
                    <p>This is a reminder that your task is due tomorrow:</p>
                    <p style="background-color: #f9f9f9; padding: 10px; border-left: 4px solid #E74C3C;">
                        %s
                    </p>
                    <p>Best Regards,</p>
                    <p><strong>Task Manager</strong></p>
                </body>
                </html>
                """, taskName);
    }
}

