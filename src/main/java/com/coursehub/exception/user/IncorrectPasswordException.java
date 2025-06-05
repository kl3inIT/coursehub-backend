package com.coursehub.exception.user;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException() {
        super("Current password is incorrect");
    }
} 