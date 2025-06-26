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
    private Boolean completed;// Đã đăng ký hay chưa
    private Date enrollDate;
    private Double progress;
    private Boolean canAccess; // Có thể truy cập course không (enrolled HOẶC manager/admin)
    private String accessReason; // Lý do có thể truy cập
}
