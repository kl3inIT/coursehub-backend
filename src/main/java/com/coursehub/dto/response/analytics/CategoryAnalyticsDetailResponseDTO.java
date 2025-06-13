package com.coursehub.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryAnalyticsDetailResponseDTO {
    private Long categoryId;
    private String categoryName;
    private String description;
    private Long courseCount;
    private Double averageRating;
    private Long totalStudents;
    private BigDecimal totalRevenue;
    private Date createdDate;
    private Date modifiedDate;
} 