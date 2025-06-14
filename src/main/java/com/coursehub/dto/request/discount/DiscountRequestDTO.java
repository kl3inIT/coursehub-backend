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

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "start date must not be null")
    private Date startDate;

    @NotNull(message = "end date must not be null")
    private Date endDate;

    @NotNull(message = "percentage must not be null")
    @DecimalMin(value = "1.0", inclusive = true, message = "percentage must be at least 1")
    @DecimalMax(value = "100.0", inclusive = true, message = "percentage must be at most 100")
    private Double percentage;

    @NotNull(message = "isActive must not be null")
    private Long isActive;

    @NotNull(message = "quantity must not be null")
    @Min(value = 1, message = "quantity must be at least 1")
    private Long quantity;

    @NotNull(message = "Category list must not be null")
    @Size(min = 1, message = "There must be at least one category")
    private List<Long> categoryIds;

    @NotNull(message = "Course list must not be null")
    @Size(min = 1, message = "There must be at least one course")
    private List<Long> courseIds;

}
