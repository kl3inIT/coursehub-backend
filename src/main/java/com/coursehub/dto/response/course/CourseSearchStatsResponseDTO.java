package com.coursehub.dto.response.course;

import lombok.*;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class CourseSearchStatsResponseDTO {

    private Long totalCourses;
    private Long minPrice;
    private Long maxPrice;
    private Long avgRating;
    
    // Level statistics - only course count per level
    private Map<String, Long> levelStats;
}
