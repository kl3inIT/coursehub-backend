package com.coursehub.dto.request.course;

import com.coursehub.enums.CourseLevel;
import com.coursehub.utils.validator.EnumValue;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSearchRequestDTO {
    private String searchTerm;
    
    private Long categoryId;
    
    @EnumValue(name = "level", enumClass = CourseLevel.class)
    private String level;
    
    @Min(value = 0, message = "Minimum price cannot be negative")
    private Double minPrice;
    
    @Min(value = 0, message = "Maximum price cannot be negative")
    private Double maxPrice;
    
    private String sortBy;
    private String sortDirection;
    
    // New fields for advanced search
    private Double minRating;
    private Boolean isFree;
    private Boolean isDiscounted;
    private String status;

    
    // Validation method
    public void validatePriceRange() {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
    }
} 