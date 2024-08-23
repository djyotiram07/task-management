package com.example.exceptions;

public class ProjectProgressNotFound extends RuntimeException {

    public ProjectProgressNotFound(String message) {
        super(message);
    }
}
