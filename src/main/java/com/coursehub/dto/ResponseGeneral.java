package com.coursehub.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGeneral<T> {
    private T data;
    private String message;
    private String detail;
}
