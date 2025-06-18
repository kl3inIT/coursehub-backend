package com.coursehub.service.impl;

import com.coursehub.dto.response.analytics.CategoryAnalyticsDetailResponseDTO;
import com.coursehub.dto.response.analytics.CourseAnalyticsDetailResponseDTO;
import com.coursehub.exceptions.analytics.AnalyticsRetrievalException;
import com.coursehub.repository.AnalyticsRepository;
import com.coursehub.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryAnalyticsDetailResponseDTO> getCategoryAnalyticsDetails(
            Date startDate,
            Date endDate,
            Pageable pageable) throws AnalyticsRetrievalException {
        try {
            // 1. Fetch current period data
            Page<CategoryAnalyticsDetailResponseDTO> currentPeriodPage =
                    analyticsRepository.getCategoryAnalyticsDetails(startDate, endDate, pageable);

            Double totalOverallRevenueCurrent = analyticsRepository.getTotalRevenue(startDate, endDate);

            // 2. Process current period data, calculate revenue proportion
            List<CategoryAnalyticsDetailResponseDTO> processedList = currentPeriodPage.getContent().stream()
                    .map(currentDto -> {
                        // Calculate revenue proportion for current period
                        if (totalOverallRevenueCurrent != null && totalOverallRevenueCurrent > 0) {
                            double revenueProportion = (currentDto.getTotalRevenue() / totalOverallRevenueCurrent) * 100;
                            // Format to 2 decimal places
                            currentDto.setRevenueProportion(Double.parseDouble(String.format("%.2f", revenueProportion)));
                        } else {
                            currentDto.setRevenueProportion(0.0);
                        }
                        return currentDto;
                    })
                    .toList();

            return new PageImpl<>(processedList, pageable, currentPeriodPage.getTotalElements());

        } catch (Exception e) {
            throw new AnalyticsRetrievalException("Failed to retrieve category analytics details: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseAnalyticsDetailResponseDTO> getCourseAnalyticsDetails(
            Date startDate,
            Date endDate,
            Pageable pageable) throws AnalyticsRetrievalException {
        try {
            log.info("Fetching course analytics details for period: {} to {}", startDate, endDate);

            // 1. Fetch course analytics data với revenue sorting handled trong repository
            Page<CourseAnalyticsDetailResponseDTO> courseAnalyticsPage =
                    analyticsRepository.getCourseAnalyticsDetails(startDate, endDate, pageable);

            // 2. Tính tổng revenue của tất cả course trong khoảng thời gian
            Double totalCourseRevenue = analyticsRepository.getTotalCourseRevenue(startDate, endDate);
            
            log.debug("Total course revenue for period: {}", totalCourseRevenue);

            // 3. Tính revenuePercent cho từng course
            List<CourseAnalyticsDetailResponseDTO> processedList = courseAnalyticsPage.getContent().stream()
                    .map(courseDto -> {
                        // Tính revenue percent = (course revenue / total revenue) * 100
                        if (totalCourseRevenue != null && totalCourseRevenue > 0 && courseDto.getRevenue() != null) {
                            double revenuePercent = (courseDto.getRevenue() / totalCourseRevenue) * 100;
                            // Format to 2 decimal places
                            courseDto.setRevenuePercent(Double.parseDouble(String.format("%.2f", revenuePercent)));
                        } else {
                            courseDto.setRevenuePercent(0.0);
                        }
                        return courseDto;
                    })
                    .collect(Collectors.toList());

            log.info("Successfully processed {} course analytics records", processedList.size());

            return new PageImpl<>(processedList, pageable, courseAnalyticsPage.getTotalElements());

        } catch (Exception e) {
            log.error("Failed to retrieve course analytics details", e);
            throw new AnalyticsRetrievalException("Failed to retrieve course analytics details: " + e.getMessage(), e);
        }
    }

}