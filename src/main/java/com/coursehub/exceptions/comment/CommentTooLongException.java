package com.coursehub.exceptions.comment;

public class CommentTooLongException extends RuntimeException{
    public CommentTooLongException(String message) {
        super(message);
    }
}
