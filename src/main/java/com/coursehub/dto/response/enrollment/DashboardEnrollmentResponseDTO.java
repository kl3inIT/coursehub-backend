package com.coursehub.dto.response.enrollment;

import lombok.*;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class DashboardEnrollmentResponseDTO {
    private Long id;
    private String courseTitle;
    private String courseDescription;
    private String instructorName;
    private Long courseId;
    private String courseThumbnail;
    private Date completedDate;
}
