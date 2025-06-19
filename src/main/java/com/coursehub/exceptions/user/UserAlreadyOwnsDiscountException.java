package com.coursehub.exceptions.user;

public class UserAlreadyOwnsDiscountException extends RuntimeException {
    public UserAlreadyOwnsDiscountException(String message) {
        super(message);
    }
}
