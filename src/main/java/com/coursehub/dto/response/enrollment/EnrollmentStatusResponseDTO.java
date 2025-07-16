package com.coursehub.dto.response.enrollment;

import lombok.*;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class EnrollmentStatusResponseDTO {

    private Boolean enrolled;
    private Boolean completed;
    private Date enrollDate;
    private Double progress;
    private Boolean canAccess; 
    private String accessReason; 
}
