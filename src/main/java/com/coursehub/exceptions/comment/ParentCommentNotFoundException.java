package com.coursehub.exceptions.comment;

public class ParentCommentNotFoundException extends RuntimeException {
    public ParentCommentNotFoundException(String message) {
        super(message);
    }
}
