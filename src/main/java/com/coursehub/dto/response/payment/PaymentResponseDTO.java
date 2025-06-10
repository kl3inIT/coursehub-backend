package com.coursehub.dto.response.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentResponseDTO {
    private String transactionCode;
    private String bankNumber;
    private String bankCode;
    private String accountHolder;
    private Double amount;

}
