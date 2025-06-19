package com.coursehub.dto.response.discount;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class DiscountSearchResponseDTO {
    private Long id;
    private String description;
    private Double percentage;
    private int totalCategory;
    private int totalCourse;
    private Long usage;
    private Long quantity;
    private Long availableQuantity;
    private List<Long> categoryIds;
    private List<Long> courseIds;
    private Date startDate;
    private Date endDate;
    private Long isActive;
    private String status;
}
