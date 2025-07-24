package com.coursehub.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDTO {

    @NotBlank(message = "Course category name is required")
    @Size(min = 5, max = 30, message = "Name must be between 5 and 30 characters")
    private String name;

    @NotBlank(message = "Course category description is required")
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    private String description;
}
