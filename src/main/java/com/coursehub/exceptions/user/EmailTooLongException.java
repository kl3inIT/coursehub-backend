package com.coursehub.exceptions.user;

public class EmailTooLongException extends RuntimeException {
    public EmailTooLongException(String message) {
        super(message);
    }
}
