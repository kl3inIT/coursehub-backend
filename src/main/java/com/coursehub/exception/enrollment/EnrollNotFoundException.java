package com.coursehub.exception.enrollment;

public class EnrollNotFoundException extends RuntimeException {
    public EnrollNotFoundException(String message) {
        super(message);
    }
}
