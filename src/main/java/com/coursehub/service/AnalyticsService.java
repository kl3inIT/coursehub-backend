package com.coursehub.service;

import com.coursehub.dto.response.analytics.CategoryAnalyticsDetailResponseDTO;
import com.coursehub.dto.response.analytics.CourseAnalyticsDetailResponseDTO;
import com.coursehub.exceptions.analytics.AnalyticsRetrievalException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface AnalyticsService {
    Page<CategoryAnalyticsDetailResponseDTO> getCategoryAnalyticsDetails(
            Date startDate,
            Date endDate,
            Pageable pageable) throws AnalyticsRetrievalException;

    Page<CourseAnalyticsDetailResponseDTO> getCourseAnalyticsDetails(
            Date startDate,
            Date endDate,
            Pageable pageable) throws AnalyticsRetrievalException;
} 