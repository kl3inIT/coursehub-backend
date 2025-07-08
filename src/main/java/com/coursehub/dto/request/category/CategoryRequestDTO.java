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
    @Size(max = 30, message = "Name must be at most 30 characters")
    private String name;

    @NotBlank(message = "Course category description is required")
    @Size(max = 200, message = "Description must be at most 200 characters")
    private String description;
}
