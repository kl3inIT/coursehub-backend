package com.coursehub.dto.response.enrollment;

import lombok.*;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class EnrollmentResponseDTO {
    private Long id;
    private String courseTitle;
    private String courseDescription;
    private Long courseId;
    private String courseThumbnail;

}
