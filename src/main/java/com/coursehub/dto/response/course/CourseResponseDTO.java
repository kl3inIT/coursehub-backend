package com.coursehub.dto.response.course;

import com.coursehub.enums.CourseLevel;
import lombok.*;

import java.io.Serializable;
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

    private CourseLevel courseLevel;
    private Integer duration;
    private Double finalPrice;

    private Boolean isActive;
    // Instructor information
    private String instructorName;
    // Statistics
    private Double averageRating;
    private Long totalReviews;
    private Long totalStudents;
    private Long totalLessons;

}
