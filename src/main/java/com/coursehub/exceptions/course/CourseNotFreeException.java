package com.coursehub.exceptions.course;

public class CourseNotFreeException extends RuntimeException {
    public CourseNotFreeException(String message) {
        super(message);
    }
} 