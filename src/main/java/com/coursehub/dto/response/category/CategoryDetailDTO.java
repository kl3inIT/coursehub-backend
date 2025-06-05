package com.coursehub.dto.response.category;

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
public class CategoryDetailDTO {
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