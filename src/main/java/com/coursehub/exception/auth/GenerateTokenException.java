package com.coursehub.exception.auth;

public class GenerateTokenException extends RuntimeException {
    public GenerateTokenException(String message) {
        super(message);
    }
}
