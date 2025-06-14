package com.coursehub.dto.response.discount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountVerifyResponseDTO {
    private Double percentage;
    private Boolean isValid;

}
