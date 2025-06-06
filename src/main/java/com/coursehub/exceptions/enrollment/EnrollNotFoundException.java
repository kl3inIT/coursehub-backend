package com.coursehub.exceptions.enrollment;

public class EnrollNotFoundException extends RuntimeException {
    public EnrollNotFoundException(String message) {
        super(message);
    }
}
