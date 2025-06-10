package com.coursehub.service.impl;

import com.coursehub.dto.response.analytics.CourseAnalyticsDetailResponseDTO;
import com.coursehub.dto.response.analytics.CourseAnalyticsChartResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.repository.CourseAnalyticsRepository;
import com.coursehub.service.AnalyticsService;
import com.coursehub.service.PaymentService;
import com.coursehub.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final CourseAnalyticsRepository courseAnalyticsRepository;
    private final PaymentService paymentService;
    private final ReviewService reviewService;

    @Override
    @Transactional(readOnly = true)
    public CourseAnalyticsDetailResponseDTO getCourseAnalyticsDetail(Long courseId) {
        CourseEntity course = courseAnalyticsRepository.findCourseById(courseId);
        Map<String, Long> enrollmentStats = courseAnalyticsRepository.getEnrollmentStats(courseId);
        Long totalEnrollments = enrollmentStats.getOrDefault("enrollmentCount", 0L);
        Long completedStudents = 0L; // TODO: Implement after adding status field to EnrollmentEntity
        Double completionRate = 0.0;

        if (totalEnrollments > 0) {
            completionRate = (double) completedStudents / totalEnrollments * 100;
        }

        Double averageRating = reviewService.getAverageRating(courseId);
        BigDecimal totalRevenue = paymentService.getTotalRevenueByCourseId(courseId);
        BigDecimal averageRevenuePerStudent = totalEnrollments > 0 
            ? totalRevenue.divide(new BigDecimal(totalEnrollments), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        return CourseAnalyticsDetailResponseDTO.builder()
                .courseId(course.getId())
                .courseName(course.getTitle())
                .description(course.getDescription())
                .categoryName(course.getCategoryEntity().getName())
                .level(course.getLevel().getLevelName())
                .status(course.getStatus().getStatusName())
                .price(course.getPrice())
                .thumbnail(course.getThumbnail())
                .totalEnrollments(totalEnrollments)
                .completedStudents(completedStudents)
                .completionRate(completionRate)
                .averageRating(averageRating)
                .totalReviews((long) course.getReviewEntities().size())
                .totalRevenue(totalRevenue)
                .averageRevenuePerStudent(averageRevenuePerStudent)
                .createdDate(course.getCreatedDate())
                .lastModifiedDate(course.getModifiedDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseAnalyticsChartResponseDTO> getCourseAnalyticsChart() {
        return courseAnalyticsRepository.findAllCourses().stream()
                .map(course -> CourseAnalyticsChartResponseDTO.builder()
                        .courseName(course.getTitle())
                        .enrollmentCount((long) course.getEnrollmentEntities().size())
                        .completionRate(calculateCompletionRate(course))
                        .revenue(paymentService.getTotalRevenueByCourseId(course.getId()))
                        .averageRating(reviewService.getAverageRating(course.getId()))
                        .totalReviews((long) course.getReviewEntities().size())
                        .level(course.getLevel().getLevelName())
                        .status(course.getStatus().getStatusName())
                        .price(course.getPrice())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseAnalyticsChartResponseDTO> getTopCoursesByEnrollment(int limit) {
        return courseAnalyticsRepository.findAllCourses().stream()
                .map(course -> CourseAnalyticsChartResponseDTO.builder()
                        .courseName(course.getTitle())
                        .enrollmentCount((long) course.getEnrollmentEntities().size())
                        .completionRate(calculateCompletionRate(course))
                        .revenue(paymentService.getTotalRevenueByCourseId(course.getId()))
                        .averageRating(reviewService.getAverageRating(course.getId()))
                        .totalReviews((long) course.getReviewEntities().size())
                        .level(course.getLevel().getLevelName())
                        .status(course.getStatus().getStatusName())
                        .price(course.getPrice())
                        .build())
                .sorted(Comparator.comparing(CourseAnalyticsChartResponseDTO::getEnrollmentCount).reversed())
                .limit(limit)
                .toList();
    }

    private Double calculateCompletionRate(CourseEntity course) {
        long totalEnrollments = course.getEnrollmentEntities().size();
        if (totalEnrollments == 0) {
            return 0.0;
        }
        // TODO: Implement after adding status field to EnrollmentEntity
        long completedEnrollments = 0;
        return (double) completedEnrollments / totalEnrollments * 100;
    }
} 