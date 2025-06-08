package com.coursehub.service;

import com.coursehub.dto.response.analytics.CourseAnalyticsDetailResponseDTO;
import com.coursehub.dto.response.analytics.CourseAnalyticsChartResponseDTO;

import java.util.List;

public interface AnalyticsService {
    CourseAnalyticsDetailResponseDTO getCourseAnalyticsDetail(Long courseId);
    List<CourseAnalyticsChartResponseDTO> getCourseAnalyticsChart();
    List<CourseAnalyticsChartResponseDTO> getTopCoursesByEnrollment(int limit);
} 