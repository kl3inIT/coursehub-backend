package com.coursehub.service.impl;

import com.coursehub.dto.response.analytics.CategoryAnalyticsDetailResponseDTO;
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
                    .collect(Collectors.toList());

            return new PageImpl<>(processedList, pageable, currentPeriodPage.getTotalElements());

        } catch (Exception e) {
            log.error("Error in getCategoryAnalyticsDetails: {}", e.getMessage(), e);
            throw new AnalyticsRetrievalException("Failed to retrieve category analytics details: " + e.getMessage(), e);
        }
    }
}