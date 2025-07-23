package com.coursehub.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnalyticsDetailResponseDTO {
    // ID khóa học
    private Long id;
    
    // Tên khóa học
    private String courseName;
    
    // Số học viên mới trong kỳ hiện tại
    private Integer newStudents;
    
    // Số học viên trong kỳ trước (để so sánh)
    private Integer previousCompletion;
    
    // Tỷ lệ tăng trưởng học viên so với kỳ trước (đơn vị: %)
    private Double growth;
    
    // Số lượng đánh giá (reviews) của khóa học
    private Integer reviews;
    
    // Điểm đánh giá trung bình của khóa học
    private Double avgRating;

} 