package com.coursehub.exception.category;

public class CategoryNotFoundExeption extends RuntimeException {
    public CategoryNotFoundExeption(String message) {
        super(message);
    }
}
