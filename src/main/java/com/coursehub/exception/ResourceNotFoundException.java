package com.coursehub.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ResourceNotFoundException forCourse(Long courseId) {
        return new ResourceNotFoundException("Course not found with ID: " + courseId);
    }

    public static ResourceNotFoundException forUser(Long userId) {
        return new ResourceNotFoundException("User not found with ID: " + userId);
    }

    public static ResourceNotFoundException forLesson(Long lessonId) {
        return new ResourceNotFoundException("Lesson not found with ID: " + lessonId);
    }
} 