package com.coursehub.dto.response.course;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class CourseResponseDTO {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private String thumbnailUrl;
    private String category;
    private String level;
    private BigDecimal finalPrice;
    private String status;
    private String instructorName;
    private Double averageRating;
    private Long totalReviews;
    private Long totalStudents;
    private Long totalLessons;

}
