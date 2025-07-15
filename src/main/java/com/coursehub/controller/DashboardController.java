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

    @GetMapping("/manager")
    public ResponseEntity<ResponseGeneral<DashboardManagerResponseDTO>> getManagerDashboard() {
        DashboardManagerResponseDTO data = dashboardService.getManagerDashboard();
        ResponseGeneral<DashboardManagerResponseDTO> response = new ResponseGeneral<>();
        response.setData(data);
        response.setMessage(SUCCESS);
        response.setDetail("Dashboard data retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/manager/stats")
    public ResponseEntity<ResponseGeneral<DashboardManagerResponseDTO>> getManagerDashboardStats(@RequestParam int month, @RequestParam int year) {
        DashboardManagerResponseDTO data = dashboardService.getManagerDashboardStats(month, year);
        ResponseGeneral<DashboardManagerResponseDTO> response = new ResponseGeneral<>();
        response.setData(data);
        response.setMessage(SUCCESS);
        response.setDetail("Dashboard stats (4 cards) retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/manager/revenue-chart")
    public ResponseEntity<ResponseGeneral<List<Double>>> getRevenueChart(@RequestParam int year) {
        List<Double> data = dashboardService.getMonthlyRevenueByYear(year);
        ResponseGeneral<List<Double>> response = new ResponseGeneral<>();
        response.setData(data);
        response.setMessage(SUCCESS);
        response.setDetail("Revenue chart data retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/manager/new-courses-chart")
    public ResponseEntity<ResponseGeneral<List<Long>>> getNewCoursesChart(@RequestParam int year) {
        List<Long> data = dashboardService.getMonthlyNewCoursesByYear(year);
        ResponseGeneral<List<Long>> response = new ResponseGeneral<>();
        response.setData(data);
        response.setMessage(SUCCESS);
        response.setDetail("New courses chart data retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/manager/student-enrollment-chart")
    public ResponseEntity<ResponseGeneral<List<Long>>> getStudentEnrollmentChart(@RequestParam int year) {
        List<Long> data = dashboardService.getMonthlyStudentEnrollmentsByYear(year);
        ResponseGeneral<List<Long>> response = new ResponseGeneral<>();
        response.setData(data);
        response.setMessage(SUCCESS);
        response.setDetail("Student enrollment chart data retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/manager/top-courses")
    public ResponseEntity<ResponseGeneral<List<TopCourse>>> getTopCoursesByMonthYear(@RequestParam int month, @RequestParam int year) {
        List<TopCourse> data = dashboardService.getTopCoursesByMonthYear(month, year);
        ResponseGeneral<List<TopCourse>> response = new ResponseGeneral<>();
        response.setData(data);
        response.setMessage(SUCCESS);
        response.setDetail("Top courses data retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/manager/revenue-insights")
    public ResponseEntity<ResponseGeneral<DashboardManagerResponseDTO>> getRevenueInsightsByMonthYear(@RequestParam int month, @RequestParam int year) {
        DashboardManagerResponseDTO data = dashboardService.getRevenueInsightsByMonthYear(month, year);
        ResponseGeneral<DashboardManagerResponseDTO> response = new ResponseGeneral<>();
        response.setData(data);
        response.setMessage(SUCCESS);
        response.setDetail("Revenue insights data retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 