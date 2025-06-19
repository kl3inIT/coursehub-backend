package com.coursehub.exceptions.course;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(String message) {
        super(message);
    }
    
    public CourseNotFoundException(Long courseId) {
        super("Course not found with ID: " + courseId);
    }
} 