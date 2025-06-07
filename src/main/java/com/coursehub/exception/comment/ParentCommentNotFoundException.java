package com.coursehub.exception.comment;

public class ParentCommentNotFoundException extends RuntimeException {
    public ParentCommentNotFoundException(String message) {
        super(message);
    }
}
