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
    private String code;
    private String description;
    private Double percentage;
    private int totalCategory;
    private int totalCourse;
    private List<Long> categoryIds;
    private List<Long> courseIds;
    private String usage;
    private Date expiryTime;
    private Long isActive;
    private Long isGlobal;
}
