package com.coursehub.exceptions.discount;

public class DiscountDeletionNotAllowedException extends RuntimeException {
    public DiscountDeletionNotAllowedException(String message) {
        super(message);
    }
}
