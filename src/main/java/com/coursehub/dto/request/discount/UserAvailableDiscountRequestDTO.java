package com.coursehub.dto.request.discount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAvailableDiscountRequestDTO {
    private Long discountId;
    private Long courseId;
}
