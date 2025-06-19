package com.coursehub.exceptions.course;

public class CourseAlreadyArchivedException extends RuntimeException {
    public CourseAlreadyArchivedException(String message) {
        super(message);
    }
}
