package com.coursehub.exceptions.announcement;

public class ContentTooLongException extends RuntimeException {
    public ContentTooLongException(String message) {
        super(message);
    }
}
