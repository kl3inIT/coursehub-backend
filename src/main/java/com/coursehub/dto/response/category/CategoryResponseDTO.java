package com.coursehub.dto.response.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {
    private String name;
    private String description;
    private Long courseCount;
    private Date createdDate;
    private Date modifiedDate;
}
