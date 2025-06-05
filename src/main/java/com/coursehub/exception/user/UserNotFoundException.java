package com.coursehub.exception.user;

public class UserNotFoundException extends RuntimeException{
     public UserNotFoundException(String message) {
          super(message);
     }

}
