package com.coursehub.exceptions.lesson;

public class LessonProgressNotFoundException extends RuntimeException {
    public LessonProgressNotFoundException(String message) {
        super(message);
    }
}
