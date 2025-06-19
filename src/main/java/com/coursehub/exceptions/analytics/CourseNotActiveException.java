package com.coursehub.exceptions.analytics;

public class CourseNotActiveException extends RuntimeException {
    public CourseNotActiveException(String message) {
        super(message);
    }
} 