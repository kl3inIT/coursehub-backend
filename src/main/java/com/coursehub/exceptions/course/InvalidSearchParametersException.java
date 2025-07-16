package com.coursehub.exceptions.course;

public class InvalidSearchParametersException extends RuntimeException {
    public InvalidSearchParametersException(String message) {
        super(message);
    }
    
    public InvalidSearchParametersException(String message, Throwable cause) {
        super(message, cause);
    }
} 