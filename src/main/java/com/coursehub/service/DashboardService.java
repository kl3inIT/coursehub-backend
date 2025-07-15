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
    List<Long> getMonthlyStudentEnrollments();
    List<TopCourse> getTopCourses();
    DashboardManagerResponseDTO getManagerDashboard();
    DashboardManagerResponseDTO getManagerDashboardStats(int month, int year);
    List<Double> getMonthlyRevenueByYear(int year);
    List<Long> getMonthlyNewCoursesByYear(int year);
    List<Long> getMonthlyStudentEnrollmentsByYear(int year);
    List<TopCourse> getTopCoursesByMonthYear(int month, int year);
    DashboardManagerResponseDTO getRevenueInsightsByMonthYear(int month, int year);
}
