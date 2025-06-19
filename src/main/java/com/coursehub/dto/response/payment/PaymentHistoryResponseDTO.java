package com.coursehub.dto.response.payment;


import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryResponseDTO {

    private Long id;
    private String transactionCode;
    private String courseName;
    private String userName;
    private Double amount;
    private String status;
    private Date date;

}
