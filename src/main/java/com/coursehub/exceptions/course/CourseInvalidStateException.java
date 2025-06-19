package com.coursehub.exceptions.course;

public class CourseInvalidStateException extends RuntimeException {
    public CourseInvalidStateException(String message) {
        super(message);
    }
}
