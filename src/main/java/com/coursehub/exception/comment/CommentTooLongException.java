package com.coursehub.exception.comment;

public class CommentTooLongException extends RuntimeException{
    public CommentTooLongException(String message) {
        super(message);
    }
}
