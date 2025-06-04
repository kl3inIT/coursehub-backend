package com.coursehub.dto.response.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDTO {
    private String transactionCode;
    private String bankNumber;
    private String bankCode;
    private Double amount;

}
