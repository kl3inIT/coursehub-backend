package com.coursehub.exceptions.course;

public class SearchOperationException extends RuntimeException {
    public SearchOperationException(String message) {
        super(message);
    }
    
    public SearchOperationException(String message, Throwable cause) {
        super(message, cause);
    }
} 