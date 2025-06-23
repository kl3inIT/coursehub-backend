package com.coursehub.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseBasicDTO {
    private Long id;
    private String title;
    private String thumbnail;
} 