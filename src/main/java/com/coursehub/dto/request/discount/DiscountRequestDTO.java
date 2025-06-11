package com.coursehub.dto.request.discount;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DiscountRequestDTO {
    private Long id;

    @NotBlank(message = "code must not be blank")
    private String code;

    private String description;

    @NotNull(message = "expiryDate must not be null")
    private Date expiryDate;

    @NotNull(message = "percentage must not be null")
    @DecimalMin(value = "1.0", inclusive = true, message = "percentage must be at least 1")
    @DecimalMax(value = "100.0", inclusive = true, message = "percentage must be at most 100")
    private Double percentage;

    @NotNull(message = "isActive must not be null")
    private Long isActive;

    @NotNull(message = "isGlobal must not be null")
    private Long isGlobal;

    @NotNull(message = "quantity must not be null")
    @Min(value = 1, message = "quantity must be at least 1")
    private Long quantity;

    private List<Long> categoryIds;

    private List<Long> courseIds;

}
