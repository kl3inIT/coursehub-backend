package com.coursehub.exceptions.lesson;

public class PreviousLessonNotFoundException extends RuntimeException {
    public PreviousLessonNotFoundException(String message) {
        super(message);
    }
}
