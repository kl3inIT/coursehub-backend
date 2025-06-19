package com.coursehub.exceptions.module;

public class PreviousModuleNotFoundException extends RuntimeException {
    public PreviousModuleNotFoundException(String message) {
        super(message);
    }
}
