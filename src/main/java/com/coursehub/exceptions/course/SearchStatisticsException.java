package com.coursehub.exceptions.course;

public class SearchStatisticsException extends RuntimeException {
    public SearchStatisticsException(String message) {
        super(message);
    }
    
    public SearchStatisticsException(String message, Throwable cause) {
        super(message, cause);
    }
} 