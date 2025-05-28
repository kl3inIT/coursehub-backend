package com.coursehub.exception.course;

public class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super(message);
    }
    
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
} 