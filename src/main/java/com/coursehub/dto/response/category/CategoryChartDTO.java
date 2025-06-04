package com.coursehub.dto.response.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryChartDTO {
    private String categoryName;
    private Long courseCount;
    private Double percentage;
} 