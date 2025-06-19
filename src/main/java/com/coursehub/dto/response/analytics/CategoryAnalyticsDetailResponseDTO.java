package com.coursehub.dto.response.analytics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CategoryAnalyticsDetailResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Long courseCount;
    private Long totalStudents;
    private Double totalRevenue;
    private Double revenueProportion;
    private Date createdDate;
    private Date modifiedDate;

    public CategoryAnalyticsDetailResponseDTO(
            Long id,
            String name,
            String description,
            Long courseCount,
            Long totalStudents,
            Double totalRevenue,
            Double revenueProportion,
            Date createdDate,
            Date modifiedDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.courseCount = courseCount;
        this.totalStudents = totalStudents;
        this.totalRevenue = totalRevenue;
        this.revenueProportion = revenueProportion;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}