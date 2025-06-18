package com.coursehub.service.impl;

import com.coursehub.dto.response.analytics.CategoryAnalyticsDetailResponseDTO;
import com.coursehub.dto.response.analytics.CourseAnalyticsDetailResponseDTO;
import com.coursehub.dto.response.analytics.StudentAnalyticsDetailResponseDTO;
import com.coursehub.entity.CourseEntity;
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

import java.util.*;
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

    @Override
    @Transactional(readOnly = true)
    public Page<StudentAnalyticsDetailResponseDTO> getStudentAnalyticsDetails(
            Date startDate,
            Date endDate,
            Pageable pageable) throws AnalyticsRetrievalException {
        try {
            log.info("Fetching student analytics details for period: {} to {}", startDate, endDate);

            // 1. Calculate previous period dates for comparison
            Date[] previousPeriod = calculatePreviousPeriod(startDate, endDate);
            Date previousStartDate = previousPeriod[0];
            Date previousEndDate = previousPeriod[1];

            log.debug("Previous period: {} to {}", previousStartDate, previousEndDate);

            // 2. Get all courses (we'll do pagination manually after sorting)
            Page<CourseEntity> coursesPage = analyticsRepository.getAllCoursesForStudentAnalytics(
                    org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)
            );

            // 3. Build analytics data for each course
            List<StudentAnalyticsDetailResponseDTO> studentAnalyticsList = coursesPage.getContent().stream()
                    .map(course -> {
                        // Get current period data
                        Integer newStudents = analyticsRepository.getNewStudentsByCourse(
                                course.getId(), startDate, endDate);
                        Integer reviews = analyticsRepository.getReviewsCountByCourse(
                                course.getId(), startDate, endDate);
                        Double avgRating = analyticsRepository.getAvgRatingByCourse(
                                course.getId(), startDate, endDate);

                        // Get previous period data
                        Integer previousStudents = analyticsRepository.getPreviousStudentsByCourse(
                                course.getId(), previousStartDate, previousEndDate);

                        log.debug("Course {}: newStudents={}, previousStudents={}, reviews={}, avgRating={}", 
                                course.getTitle(), newStudents, previousStudents, reviews, avgRating);

                        // Calculate growth rate
                        Double growth = calculateGrowthRate(newStudents, previousStudents);

                        // Format avgRating to 2 decimal places
                        Double formattedAvgRating = avgRating != null ? 
                                Double.parseDouble(String.format("%.2f", avgRating)) : 0.0;

                        return new StudentAnalyticsDetailResponseDTO(
                                course.getId(),
                                course.getTitle(),
                                newStudents != null ? newStudents : 0,
                                previousStudents != null ? previousStudents : 0,
                                growth,
                                reviews != null ? reviews : 0,
                                formattedAvgRating
                        );
                    })
                    // 4. Sort according to requirements:
                    // 1. Growth DESC, 2. New Students DESC, 3. Reviews DESC, 4. Avg Rating DESC, 5. Course Name ASC
                    .sorted(Comparator
                            .comparing(StudentAnalyticsDetailResponseDTO::getGrowth, Comparator.reverseOrder())
                            .thenComparing(StudentAnalyticsDetailResponseDTO::getNewStudents, Comparator.reverseOrder())
                            .thenComparing(StudentAnalyticsDetailResponseDTO::getReviews, Comparator.reverseOrder())
                            .thenComparing(StudentAnalyticsDetailResponseDTO::getAvgRating, Comparator.reverseOrder())
                            .thenComparing(StudentAnalyticsDetailResponseDTO::getCourseName)
                    )
                    .toList();

            log.info("Successfully processed {} student analytics records", studentAnalyticsList.size());

            // 5. Apply pagination manually
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), studentAnalyticsList.size());
            List<StudentAnalyticsDetailResponseDTO> paginatedList = 
                    start >= studentAnalyticsList.size() ? 
                            Collections.emptyList() : 
                            studentAnalyticsList.subList(start, end);

            return new PageImpl<>(paginatedList, pageable, studentAnalyticsList.size());

        } catch (Exception e) {
            log.error("Failed to retrieve student analytics details", e);
            throw new AnalyticsRetrievalException("Failed to retrieve student analytics details: " + e.getMessage(), e);
        }
    }

    /**
     * Tính toán khoảng thời gian trước đó dựa trên khoảng thời gian hiện tại
     */
    private Date[] calculatePreviousPeriod(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return new Date[]{null, null};
        }

        Calendar cal = Calendar.getInstance();
        
        // Tính số ngày giữa startDate và endDate
        long diffInMillies = endDate.getTime() - startDate.getTime();
        long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);

        // Previous period có cùng độ dài, kết thúc ngay trước startDate
        cal.setTime(startDate);
        Date previousEndDate = new Date(cal.getTimeInMillis() - 1); // 1ms trước startDate

        cal.setTime(previousEndDate);
        cal.add(Calendar.DAY_OF_MONTH, -(int)diffInDays);
        Date previousStartDate = cal.getTime();

        return new Date[]{previousStartDate, previousEndDate};
    }

    /**
     * Tính tỷ lệ tăng trưởng (growth rate) với format 2 chữ số thập phân
     * Formula: ((current - previous) / previous) * 100
     * Example: current=2, previous=4 => ((2-4)/4)*100 = -50.00%
     */
    private Double calculateGrowthRate(Integer current, Integer previous) {
        log.debug("Calculating growth rate: current={}, previous={}", current, previous);
        
        // Handle null cases
        if (previous == null || previous == 0) {
            // Nếu không có data trước đó, mà hiện tại có => 100% growth
            Double result = current != null && current > 0 ? 100.0 : 0.0;
            log.debug("Previous is null/zero, returning: {}", result);
            return result;
        }
        if (current == null) {
            log.debug("Current is null, returning: -100.0");
            return -100.0;
        }
        
        // Calculate growth rate: ((current - previous) / previous) * 100
        double growth = ((double)(current - previous) / previous) * 100.0;
        Double formattedGrowth = Double.parseDouble(String.format("%.2f", growth));
        
        log.debug("Growth calculation: (({} - {}) / {}) * 100 = {}", 
                current, previous, previous, formattedGrowth);
        
        return formattedGrowth;
    }

}