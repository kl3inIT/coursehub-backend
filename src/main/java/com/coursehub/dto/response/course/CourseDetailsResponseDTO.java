package com.coursehub.dto.response.course;

import com.coursehub.dto.response.module.ModuleResponseDTO;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class CourseDetailsResponseDTO {

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
    private String updatedAt;
    private Long totalModules;
    private Long totalDuration;
    private List<ModuleResponseDTO> modules;
    private Boolean canEdit;
}
