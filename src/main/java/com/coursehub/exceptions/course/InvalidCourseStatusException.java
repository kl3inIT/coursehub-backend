package com.coursehub.exceptions.course;

public class InvalidCourseStatusException extends RuntimeException {
    public InvalidCourseStatusException(String message) {
        super(message);
    }
}
