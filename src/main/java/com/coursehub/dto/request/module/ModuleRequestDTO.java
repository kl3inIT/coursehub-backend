package com.coursehub.dto.request.module;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleRequestDTO {
    @NotBlank
    @Size(min = 3, max = 255, message = "Title length must be between 3 and 255 characters")
    private String title;
} 