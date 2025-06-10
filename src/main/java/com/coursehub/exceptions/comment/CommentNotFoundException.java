package com.coursehub.exceptions.comment;

public class CommentNotFoundException extends  RuntimeException {

    public CommentNotFoundException(String message) {
        super(message);
    }
}
