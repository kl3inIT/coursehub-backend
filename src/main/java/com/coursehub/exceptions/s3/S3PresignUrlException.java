package com.coursehub.exceptions.s3;

public class S3PresignUrlException extends RuntimeException {
    public S3PresignUrlException(String message) {
        super(message);
    }
}
