package com.coursehub.exceptions.discount;

public class DiscountDuplicateException extends RuntimeException {
    public DiscountDuplicateException(String message) {
        super(message);
    }
}
