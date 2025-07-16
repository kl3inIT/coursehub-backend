package com.coursehub.dto.response.course;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseEnrollmentResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String studentAvatar;
    private Date enrollmentDate;
    private Date lastAccessed;
    private Double progress;
    private Integer completedLessons;
    private Integer totalLessons;
    private Integer timeSpent;
    private String status;
    private Boolean certificateIssued;
    private Date completionDate;
    private Double rating;
} 