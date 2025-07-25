package com.coursehub.dto.request.course;

import com.coursehub.enums.CourseLevel;
import com.coursehub.enums.validator.EnumValue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSearchRequestDTO {

    @Size(max = 100, message = "Search term must be at most 100 characters")
    private String searchTerm;

    private List<Long> categoryIds;

    private List<String> levels;

    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum price cannot be negative")
    private Double minPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Maximum price cannot be negative")
    private Double maxPrice;

    private String sortBy;

    private String sortDirection;

    private Boolean isFree;

    private Boolean isDiscounted;

}
