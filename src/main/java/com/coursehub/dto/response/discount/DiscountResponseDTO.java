package com.coursehub.dto.response.discount;


import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountResponseDTO {
    private Long id;
    private String code;
    private Double percentage;
    private String description;
    private Date startDate;
    private Date expiryDate;
    private Long quantity;
    private Long isActive;
    private Long isGlobal;
    private Long isValid;

}
