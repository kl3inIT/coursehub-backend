package com.coursehub.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

}