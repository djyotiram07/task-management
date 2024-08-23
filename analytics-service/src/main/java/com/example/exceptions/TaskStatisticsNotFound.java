package com.example.exceptions;

public class TaskStatisticsNotFound extends RuntimeException {

    public TaskStatisticsNotFound(String message) {
        super(message);
    }
}
