package com.coursehub.dto.request.discount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountVerifyRequestDTO {
    private String code;
    private Long courseId;
}
