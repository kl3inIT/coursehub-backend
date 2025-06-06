package com.coursehub.dto.request.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDTO {
    private Long courseId;
    private String discountCode;
    private Double amount;
}
