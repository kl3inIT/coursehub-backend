package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.response.dashboard.DashboardManagerResponseDTO.TopCourse;
import com.coursehub.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

import static com.coursehub.constant.Constant.CommonConstants.SUCCESS;
import com.coursehub.dto.response.dashboard.DashboardManagerResponseDTO;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/total-categories")
    public ResponseEntity<ResponseGeneral<Long>> getTotalCategories() {
        ResponseGeneral<Long> response = new ResponseGeneral<>();
        response.setData(dashboardService.getTotalCategories());
        response.setMessage(SUCCESS);
        response.setDetail("Total categories retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/category-growth")
    public ResponseEntity<ResponseGeneral<Float>> getCategoryGrowth() {
        ResponseGeneral<Float> response = new ResponseGeneral<>();
        response.setData(dashboardService.getCategoryGrowth());
        response.setMessage(SUCCESS);
        response.setDetail("Category growth retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/total-courses")
    public ResponseEntity<ResponseGeneral<Long>> getTotalCourses() {
        ResponseGeneral<Long> response = new ResponseGeneral<>();
        response.setData(dashboardService.getTotalCourses());
        response.setMessage(SUCCESS);
        response.setDetail("Total courses retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/course-growth")
    public ResponseEntity<ResponseGeneral<Float>> getCourseGrowth() {
        ResponseGeneral<Float> response = new ResponseGeneral<>();
        response.setData(dashboardService.getCourseGrowth());
        response.setMessage(SUCCESS);
        response.setDetail("Course growth retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/total-students")
    public ResponseEntity<ResponseGeneral<Long>> getTotalStudents() {
        ResponseGeneral<Long> response = new ResponseGeneral<>();
        response.setData(dashboardService.getTotalStudents());
        response.setMessage(SUCCESS);
        response.setDetail("Total students retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/student-growth")
    public ResponseEntity<ResponseGeneral<Float>> getStudentGrowth() {
        ResponseGeneral<Float> response = new ResponseGeneral<>();
        response.setData(dashboardService.getStudentGrowth());
        response.setMessage(SUCCESS);
        response.setDetail("Student growth retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/total-revenue")
    public ResponseEntity<ResponseGeneral<BigDecimal>> getTotalRevenue() {
        ResponseGeneral<BigDecimal> response = new ResponseGeneral<>();
        response.setData(dashboardService.getTotalRevenue());
        response.setMessage(SUCCESS);
        response.setDetail("Total revenue retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/revenue-growth")
    public ResponseEntity<ResponseGeneral<Float>> getRevenueGrowth() {
        ResponseGeneral<Float> response = new ResponseGeneral<>();
        response.setData(dashboardService.getRevenueGrowth());
        response.setMessage(SUCCESS);
        response.setDetail("Revenue growth retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/total-last-month-revenue")
    public ResponseEntity<ResponseGeneral<BigDecimal>> getTotalLastMonthRevenue() {
        ResponseGeneral<BigDecimal> response = new ResponseGeneral<>();
        response.setData(dashboardService.getTotalLastMonthRevenue());
        response.setMessage(SUCCESS);
        response.setDetail("Total last month revenue retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/last-month-revenue-growth")
    public ResponseEntity<ResponseGeneral<Float>> getLastMonthRevenueGrowth() {
        ResponseGeneral<Float> response = new ResponseGeneral<>();
        response.setData(dashboardService.getLastMonthRevenueGrowth());
        response.setMessage(SUCCESS);
        response.setDetail("Last month revenue growth retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/total-three-months-ago-revenue")
    public ResponseEntity<ResponseGeneral<BigDecimal>> getTotalThreeMonthsAgoRevenue() {
        ResponseGeneral<BigDecimal> response = new ResponseGeneral<>();
        response.setData(dashboardService.getTotalThreeMonthsAgoRevenue());
        response.setMessage(SUCCESS);
        response.setDetail("Total three months ago revenue retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/three-months-ago-revenue-growth")
    public ResponseEntity<ResponseGeneral<Float>> getThreeMonthsAgoRevenueGrowth() {
        ResponseGeneral<Float> response = new ResponseGeneral<>();
        response.setData(dashboardService.getThreeMonthsAgoRevenueGrowth());
        response.setMessage(SUCCESS);
        response.setDetail("Three months ago revenue growth retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<ResponseGeneral<List<Double>>> getMonthlyRevenue() {
        ResponseGeneral<List<Double>> response = new ResponseGeneral<>();
        response.setData(dashboardService.getMonthlyRevenue());
        response.setMessage(SUCCESS);
        response.setDetail("Monthly revenue retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/monthly-new-courses")
    public ResponseEntity<ResponseGeneral<List<Long>>> getMonthlyNewCourses() {
        ResponseGeneral<List<Long>> response = new ResponseGeneral<>();
        response.setData(dashboardService.getMonthlyNewCourses());
        response.setMessage(SUCCESS);
        response.setDetail("Monthly new courses retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/top-courses")
    public ResponseEntity<ResponseGeneral<List<TopCourse>>> getTopCourses() {
        ResponseGeneral<List<TopCourse>> response = new ResponseGeneral<>();
        response.setData(dashboardService.getTopCourses());
        response.setMessage(SUCCESS);
        response.setDetail("Top courses retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/manager")
    public ResponseEntity<ResponseGeneral<DashboardManagerResponseDTO>> getManagerDashboard() {
        DashboardManagerResponseDTO data = dashboardService.getManagerDashboard();
        ResponseGeneral<DashboardManagerResponseDTO> response = new ResponseGeneral<>();
        response.setData(data);
        response.setMessage(SUCCESS);
        response.setDetail("Dashboard data retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 