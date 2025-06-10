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
    private String courseName;           // Tên khóa học
    private String description;          // Mô tả
    private String categoryName;         // Tên danh mục
    private String level;                // Cấp độ khóa học
    private String status;               // Trạng thái khóa học
    private BigDecimal price;            // Giá khóa học
    private String thumbnail;            // Ảnh thumbnail
    
    // Thống kê học viên
    private Long totalEnrollments;       // Tổng số đăng ký
    private Long completedStudents;      // Số học viên hoàn thành
    private Double completionRate;       // Tỷ lệ hoàn thành (%)
    
    // Thống kê đánh giá
    private Double averageRating;        // Đánh giá trung bình
    private Long totalReviews;           // Tổng số đánh giá
    private Map<Integer, Long> ratingDistribution; // Phân bố đánh giá (1-5 sao)
    
    // Thống kê doanh thu
    private BigDecimal totalRevenue;     // Tổng doanh thu
    private BigDecimal averageRevenuePerStudent; // Doanh thu trung bình/học viên
    
    // Thống kê thời gian
    private Date createdDate;            // Ngày tạo
    private Date lastModifiedDate;       // Ngày cập nhật cuối
    private Double averageCompletionTime; // Thời gian hoàn thành trung bình (ngày)
    
    // Thống kê nội dung
    private Integer totalLessons;        // Tổng số bài học
    private Integer totalQuizzes;        // Tổng số bài kiểm tra
    private Integer totalAssignments;    // Tổng số bài tập
    private Integer totalDuration;       // Tổng thời lượng (phút)
} 