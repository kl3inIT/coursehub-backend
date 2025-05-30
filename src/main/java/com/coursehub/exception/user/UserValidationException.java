package com.coursehub.exception.user;

public class UserValidationException extends RuntimeException{
     public UserValidationException(String message) {
          super(message);
     }

     public UserValidationException(String message, Throwable cause) {
          super(message, cause);
     }
}
