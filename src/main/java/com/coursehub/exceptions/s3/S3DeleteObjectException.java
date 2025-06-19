package com.coursehub.exceptions.s3;

public class S3DeleteObjectException extends RuntimeException {
    public S3DeleteObjectException(String message) {
        super(message);
    }
}
