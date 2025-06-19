package com.coursehub.dto.request.course;

import com.coursehub.enums.CourseLevel;
import com.coursehub.enums.validator.EnumValue;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCreationRequestDTO {

    @NotBlank(message = "Course title is required")
    @Size(min = 5, max = 100, message = "Course title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Course description is required")
    @Size(min = 20, max = 2000, message = "Course description must be between 20 and 2000 characters")
    private String description;

    @NotNull(message = "Course price is required")
    private BigDecimal price;

    @EnumValue(name = "level", enumClass = CourseLevel.class)
    private String level;

    @NotNull(message = "Course category required")
    private Long categoryCode;

}
