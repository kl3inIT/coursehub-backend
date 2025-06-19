package com.coursehub.dto.response.course;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateUpdateResponseDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private String thumbnailUrl;
    private String category;
    private String level;
    private String status;
    private Long managerId;
}
