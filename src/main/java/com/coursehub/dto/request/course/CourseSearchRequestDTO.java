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
    
    // Default values
    public static final String DEFAULT_SORT_BY = "createdDate";
    public static final String DEFAULT_SORT_DIRECTION = "desc";
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;
    
    // Sort directions
    public static final String SORT_ASC = "asc";
    public static final String SORT_DESC = "desc";
    
    // Sortable fields
    public static final String SORT_BY_TITLE = "title";
    public static final String SORT_BY_PRICE = "price";
    public static final String SORT_BY_CREATED_DATE = "createdDate";
    public static final String SORT_BY_RATING = "averageRating";
    
    // Validation method
    public void validatePriceRange() {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
    }
} 