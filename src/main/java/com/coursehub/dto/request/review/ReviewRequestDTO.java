package com.coursehub.dto.request.review;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDTO {
    
    @NotNull(message = "CourseId is required")
    @Min(value = 1, message = "CourseId must be greater than 0")
    private Long courseId;

    @NotNull(message = "Star is required")
    @Min(value = 1, message = "Star must be at least 1")
    @Max(value = 5, message = "Star must be at most 5")
    private Integer star;

    @NotBlank(message = "Comment is required")
    @Size(max = 200, message = "Comment must be at most 200 characters")
    private String comment;
} 