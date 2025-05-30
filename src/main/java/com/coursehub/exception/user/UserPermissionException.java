package com.coursehub.exception.user;

public class UserPermissionException extends RuntimeException{
     public UserPermissionException(String message) {
          super(message);
     }

     public UserPermissionException(String message, Throwable cause) {
          super(message, cause);
     }
}
