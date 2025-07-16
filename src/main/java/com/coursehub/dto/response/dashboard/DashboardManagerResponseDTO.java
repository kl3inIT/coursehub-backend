package com.coursehub.dto.response.dashboard;

import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardManagerResponseDTO {

    private Long totalCategories;
    private Float categoryGrowth;

    private Long totalCourses;
    private Float courseGrowth;

    private Long totalStudents;
    private Float studentGrowth;

    private BigDecimal totalRevenue;
    private Float revenueGrowth;

    private BigDecimal totalLastMonthRevenue;
    private Float lastMonthRevenueGrowth;

    private BigDecimal totalThreeMonthsAgoRevenue;
    private Float threeMonthsAgoRevenueGrowth;

    private List<Double> monthlyRevenue; // 12 giá trị, mỗi giá trị là doanh thu từng tháng
    private List<Long> monthlyNewCourses; // 12 giá trị, mỗi giá trị là số khoá học mới từng tháng
    private List<Long> monthlyStudentEnrollments;

    @Data
    @AllArgsConstructor
    public static class TopCourse {
        private String name;
        private Long students;
    }
    private List<TopCourse> topCourses;

}
