package com.coursehub.dto.response.analytics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RevenueAnalyticsDetailResponseDTO {
    // ID khóa học
    private Long id;
    
    // Tên khóa học
    private String courseName;
    
    // Tổng doanh thu trong khoảng thời gian được chọn (payment COMPLETED)
    private Double revenue;
    
    // Tổng doanh thu trong khoảng thời gian trước (để so sánh)
    private Double previousRevenue;
    
    // Tỷ lệ tăng trưởng doanh thu so với kỳ trước (đơn vị: %)
    private Double growth;
    
    // Số lượng đơn hàng thành công (payment COMPLETED)
    private Integer orders;
    
    // Số học viên mới (distinct userId trong payment)
    private Integer newStudents;
    
    // Tỷ trọng doanh thu (% revenue của course này trên tổng revenue tất cả course)
    private Double revenueShare;

    public RevenueAnalyticsDetailResponseDTO(
            Long id,
            String courseName,
            Double revenue,
            Double previousRevenue,
            Double growth,
            Integer orders,
            Integer newStudents,
            Double revenueShare) {
        this.id = id;
        this.courseName = courseName;
        this.revenue = revenue;
        this.previousRevenue = previousRevenue;
        this.growth = growth;
        this.orders = orders;
        this.newStudents = newStudents;
        this.revenueShare = revenueShare;
    }
}