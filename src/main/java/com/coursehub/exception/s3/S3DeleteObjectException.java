package com.coursehub.exception.s3;

public class S3DeleteObjectException extends RuntimeException {
    public S3DeleteObjectException(String message) {
        super(message);
    }
}
