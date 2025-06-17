package com.coursehub.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
    
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class CourseAnalyticsDetailResponseDTO {
    // Thông tin cơ bản
    private Long courseId;               // ID khóa học
    private String courseName;       // Tổng thời lượng (phút)
} 