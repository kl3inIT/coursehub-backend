package com.coursehub.exceptions.report;

public class ReasonTooLongException extends RuntimeException {
    public ReasonTooLongException(String message) {
        super(message);
    }
}
