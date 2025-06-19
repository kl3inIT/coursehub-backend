package com.coursehub.dto.request.payment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentHistoryRequestDTO {
    private String nameSearch;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    int page;
    int size = 5;
}
