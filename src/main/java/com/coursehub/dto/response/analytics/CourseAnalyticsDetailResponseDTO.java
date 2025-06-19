package com.coursehub.dto.response.analytics;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
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

    public CourseAnalyticsDetailResponseDTO(Long courseId, String courseName, Integer students, Double rating, Double revenue, Double revenuePercent, Long reviews, String level) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.students = students;
        this.rating = rating;
        this.revenue = revenue;
        this.revenuePercent = revenuePercent;
        this.reviews = reviews;
        this.level = level;
    }
} 