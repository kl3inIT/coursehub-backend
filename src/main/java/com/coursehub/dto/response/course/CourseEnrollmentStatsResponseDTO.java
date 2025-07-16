package com.coursehub.dto.response.course;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseEnrollmentStatsResponseDTO {
    private Integer totalEnrollments;
    private Integer activeEnrollments;
    private Integer completedEnrollments;
    private Double averageProgress;
    private Double averageTimeSpent;
    private Double completionRate;
    private Double averageRating;
} 