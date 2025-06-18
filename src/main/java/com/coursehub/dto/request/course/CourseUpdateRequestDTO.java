package com.coursehub.dto.request.course;

import com.coursehub.enums.CourseLevel;
import com.coursehub.enums.CourseStatus;
import com.coursehub.utils.validator.EnumValue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUpdateRequestDTO {

    @Size(min = 5, max = 100, message = "Course title must be between 5 and 100 characters")
    private String title;

    @Size(min = 20, max = 2000, message = "Course description must be between 20 and 2000 characters")
    private String description;

    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private BigDecimal discount;

    @EnumValue(name = "level", enumClass = CourseLevel.class)
    private String level;

    @EnumValue(name = "status", enumClass = CourseStatus.class)
    private String status;

    private Long categoryCode;
}
