package com.coursehub.dto.response.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class CourseAnalyticsChartResponseDTO {
    private String courseName;           // Tên khóa học
    private Long enrollmentCount;        // Số lượng học viên đăng ký
    private Double completionRate;       // Tỷ lệ hoàn thành khóa học (%)
    private BigDecimal revenue;          // Doanh thu
    private Double averageRating;        // Đánh giá trung bình (1-5 sao)
    private Long totalReviews;           // Tổng số đánh giá
    private String level;                // Cấp độ khóa học
    private String status;               // Trạng thái khóa học
    private BigDecimal price;            // Giá khóa học
} 