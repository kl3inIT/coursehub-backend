package com.coursehub.dto.response.course;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class DashboardCourseResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String category;
    private String instructorName;
    private Long totalDuration;
    private Long totalLessons;
    private Boolean completed;
    private Date enrollDate;
    private Date completedDate;
    private Double progress;
}
