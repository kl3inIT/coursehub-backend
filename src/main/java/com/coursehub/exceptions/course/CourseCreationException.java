package com.coursehub.exceptions.course;

public class CourseCreationException extends RuntimeException {
    public CourseCreationException(String message) {
        super(message);
    }
    
    public CourseCreationException(String message, Throwable cause) {
        super(message, cause);
    }
} 