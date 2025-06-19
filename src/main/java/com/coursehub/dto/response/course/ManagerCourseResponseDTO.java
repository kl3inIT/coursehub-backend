package com.coursehub.dto.response.course;

import lombok.*;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ManagerCourseResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String category;
    private Date lastUpdatedDate;
    private Double rating;
    private Long totalEnrollments;
    private String status;
    private Boolean canEdit;
}
