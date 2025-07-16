package com.coursehub.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSimpleResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String thumbnail;
    private Integer enrollmentCount;
} 