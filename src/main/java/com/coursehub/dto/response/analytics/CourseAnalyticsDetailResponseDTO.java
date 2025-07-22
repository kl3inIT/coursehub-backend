package com.coursehub.dto.response.analytics;

import lombok.*;

@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
public class CourseAnalyticsDetailResponseDTO {
    // Thông tin cơ bản
    private Long courseId;               // ID khóa học
    private String courseName;       // Tổng thời lượng (phút)

    // Số lượng học viên đã đăng ký khoá học
    private Integer students;

    // Điểm đánh giá trung bình của khoá học
    private Double rating;

    // Doanh thu của khoá học
    private Double revenue;

    // Tỷ lệ doanh thu của khoá học so với tổng doanh thu (đơn vị: %)
    private Double revenuePercent;

    // Số lượng đánh giá (reviews) của khoá học
    private Long reviews;

    // Trình độ khoá học (Beginner, Intermediate, Advanced...)
    private String level;
} 