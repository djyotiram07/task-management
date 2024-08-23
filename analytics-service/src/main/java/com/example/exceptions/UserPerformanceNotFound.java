package com.example.exceptions;

public class UserPerformanceNotFound extends RuntimeException {

    public UserPerformanceNotFound(String message) {
        super(message);
    }
}
