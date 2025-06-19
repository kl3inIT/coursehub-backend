package com.coursehub.exceptions.course;

public class InvalidCourseRestoreStateException extends RuntimeException {
    public InvalidCourseRestoreStateException(String message) {
        super(message);
    }
}
