package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.response.analytics.CourseAnalyticsChartResponseDTO;
import com.coursehub.dto.response.analytics.CourseAnalyticsDetailResponseDTO;
import com.coursehub.service.AnalyticsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.coursehub.constant.Constant.CommonConstants.SUCCESS;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ResponseGeneral<CourseAnalyticsDetailResponseDTO>> getCourseAnalyticsDetail(
            @PathVariable Long courseId) {
        CourseAnalyticsDetailResponseDTO detailDTO = analyticsService.getCourseAnalyticsDetail(courseId);
        ResponseGeneral<CourseAnalyticsDetailResponseDTO> response = new ResponseGeneral<>();
        response.setData(detailDTO);
        response.setMessage(SUCCESS);
        response.setDetail("Course analytics retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/courses/chart")
    public ResponseEntity<ResponseGeneral<List<CourseAnalyticsChartResponseDTO>>> getCourseAnalyticsChart() {
        List<CourseAnalyticsChartResponseDTO> chartData = analyticsService.getCourseAnalyticsChart();
        ResponseGeneral<List<CourseAnalyticsChartResponseDTO>> response = new ResponseGeneral<>();
        response.setData(chartData);
        response.setMessage(SUCCESS);
        response.setDetail("Course analytics chart data retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/courses/top/enrollment")
    public ResponseEntity<ResponseGeneral<List<CourseAnalyticsChartResponseDTO>>> getTopCoursesByEnrollment(
            @RequestParam(defaultValue = "10") int limit) {
        List<CourseAnalyticsChartResponseDTO> topCourses = analyticsService.getTopCoursesByEnrollment(limit);
        ResponseGeneral<List<CourseAnalyticsChartResponseDTO>> response = new ResponseGeneral<>();
        response.setData(topCourses);
        response.setMessage(SUCCESS);
        response.setDetail("Top courses by enrollment retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 