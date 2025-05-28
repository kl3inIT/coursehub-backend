package com.coursehub.dto.request.course;

import com.coursehub.enums.CourseLevel;
import com.coursehub.utils.validator.EnumValue;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequestDTO {

    @NotBlank(message = "Course title is required")
    @Size(min = 5, max = 100, message = "Course title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Course description is required")
    @Size(min = 20, max = 2000, message = "Course description must be between 20 and 2000 characters")
    private String description;

    @NotNull(message = "Course price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Course price must be greater than 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private BigDecimal discount;

    @EnumValue(name = "level", enumClass = CourseLevel.class)
    private String level;

    @Builder.Default
    private Boolean isActive = true;
}
