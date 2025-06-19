package com.coursehub.dto.response.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentStatusResponseDTO {
    @JsonProperty("isPaid")
    private boolean isPaid;
}
