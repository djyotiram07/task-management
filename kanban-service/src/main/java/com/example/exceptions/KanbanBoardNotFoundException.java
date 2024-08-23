package com.example.exceptions;

public class KanbanBoardNotFoundException extends RuntimeException {

    public KanbanBoardNotFoundException(String message) {
        super(message);
    }
}
