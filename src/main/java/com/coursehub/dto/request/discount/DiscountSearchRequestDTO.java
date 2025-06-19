package com.coursehub.dto.request.discount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountSearchRequestDTO {
    private Long categoryId;
    private Long courseId;
    private Long isActive;
    private String status;
    private Double percentage;
    private int page;
    private int size = 3;
}
