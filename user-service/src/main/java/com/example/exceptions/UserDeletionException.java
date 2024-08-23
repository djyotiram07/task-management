package com.example.exceptions;

public class UserDeletionException extends RuntimeException  {

    public UserDeletionException(String message) {
        super(message);
    }
}
