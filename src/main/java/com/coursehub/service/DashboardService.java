package com.coursehub.service;

import com.coursehub.dto.response.dashboard.DashboardManagerResponseDTO.TopCourse;
import com.coursehub.dto.response.dashboard.DashboardManagerResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public interface DashboardService {
    Long getTotalCategories();
    Float getCategoryGrowth();
    Long getTotalCourses();
    Float getCourseGrowth();
    Long getTotalStudents();
    Float getStudentGrowth();
    BigDecimal getTotalRevenue();
    Float getRevenueGrowth();
    BigDecimal getTotalLastMonthRevenue();
    Float getLastMonthRevenueGrowth();
    BigDecimal getTotalThreeMonthsAgoRevenue();
    Float getThreeMonthsAgoRevenueGrowth();
    List<Double> getMonthlyRevenue();
    List<Long> getMonthlyNewCourses();
    List<TopCourse> getTopCourses();
    DashboardManagerResponseDTO getManagerDashboard();
}
