package com.coursehub.dto.request.discount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountRequestDTO {
    private String code;
    private Long courseId;
}
