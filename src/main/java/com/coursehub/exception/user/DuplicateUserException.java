package com.coursehub.exception.user;

public class DuplicateUserException extends RuntimeException{
     public DuplicateUserException(String message) {
          super(message);
     }

     public DuplicateUserException(String message, Throwable cause) {
          super(message, cause);
     }

     public DuplicateUserException() {
          this("User already exists");
     }

     
}
