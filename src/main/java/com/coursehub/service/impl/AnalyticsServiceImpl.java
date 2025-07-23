package com.coursehub.service.impl;

import com.coursehub.dto.response.analytics.CategoryAnalyticsDetailResponseDTO;
import com.coursehub.dto.response.analytics.CourseAnalyticsDetailResponseDTO;
import com.coursehub.dto.response.analytics.RevenueAnalyticsDetailResponseDTO;
import com.coursehub.dto.response.analytics.StudentAnalyticsDetailResponseDTO;
import com.coursehub.entity.CategoryEntity;
import com.coursehub.entity.CourseEntity;
import com.coursehub.exceptions.analytics.AnalyticsRetrievalException;
import com.coursehub.repository.AnalyticsRepository;
import com.coursehub.repository.CategoryRepository;
import com.coursehub.repository.CourseRepository;
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
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryAnalyticsDetailResponseDTO> getCategoryAnalyticsDetails(
            Date startDate,
            Date endDate,
            Pageable pageable) throws AnalyticsRetrievalException {
        try {
            // Lấy tất cả category (phân trang thủ công)
            List<CategoryEntity> categories = categoryRepository.findAll();
            List<CategoryAnalyticsDetailResponseDTO> result = new ArrayList<>();
            Double totalRevenue = 0.0;
            // Tính tổng revenue trước để tính revenueProportion
            for (CategoryEntity category : categories) {
                Double revenue = analyticsRepository.sumRevenueByCategoryAndPeriod(category.getId(), startDate, endDate);
                if (revenue != null) totalRevenue += revenue;
            }
            for (CategoryEntity category : categories) {
                Long courseCount = analyticsRepository.countCoursesByCategoryAndPeriod(category.getId(), startDate, endDate);
                Long studentCount = analyticsRepository.countStudentsByCategoryAndPeriod(category.getId(), startDate, endDate);
                Double revenue = analyticsRepository.sumRevenueByCategoryAndPeriod(category.getId(), startDate, endDate);
                Double revenueProportion = (totalRevenue > 0 && revenue != null) ? Double.parseDouble(String.format("%.2f", (revenue / totalRevenue) * 100)) : 0.0;
                result.add(new CategoryAnalyticsDetailResponseDTO(
                    category.getId(),
                    category.getName(),
                    category.getDescription(),
                    courseCount != null ? courseCount : 0,
                    studentCount != null ? studentCount : 0,
                    revenue != null ? revenue : 0.0,
                    revenueProportion,
                    category.getCreatedDate(),
                    category.getModifiedDate()
                ));
            }
            // Phân trang thủ công
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), result.size());
            List<CategoryAnalyticsDetailResponseDTO> paged = start >= result.size() ? Collections.emptyList() : result.subList(start, end);
            return new PageImpl<>(paged, pageable, result.size());
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
            // Lấy tất cả course (phân trang thủ công)
            List<CourseEntity> courses = courseRepository.findAll();
            List<CourseAnalyticsDetailResponseDTO> result = new ArrayList<>();
            Double totalRevenue = 0.0;
            // Tính tổng revenue trước để tính revenuePercent
            for (CourseEntity course : courses) {
                Double revenue = analyticsRepository.sumRevenueByCourseAndPeriod(course.getId(), startDate, endDate);
                if (revenue != null) totalRevenue += revenue;
            }
            for (CourseEntity course : courses) {
                Integer studentCount = analyticsRepository.countStudentsByCourseAndPeriod(course.getId(), startDate, endDate);
                Double avgRating = analyticsRepository.avgRatingByCourseAndPeriod(course.getId(), startDate, endDate);
                Double revenue = analyticsRepository.sumRevenueByCourseAndPeriod(course.getId(), startDate, endDate);
                Long reviewCount = analyticsRepository.countReviewsByCourseAndPeriod(course.getId(), startDate, endDate);
                Double revenuePercent = (totalRevenue > 0 && revenue != null) ? Double.parseDouble(String.format("%.2f", (revenue / totalRevenue) * 100)) : 0.0;
                result.add(new CourseAnalyticsDetailResponseDTO(
                    course.getId(),
                    course.getTitle(),
                    studentCount != null ? studentCount : 0,
                    avgRating != null ? Double.parseDouble(String.format("%.2f", avgRating)) : 0.0,
                    revenue != null ? revenue : 0.0,
                    revenuePercent,
                    reviewCount != null ? reviewCount : 0L,
                    course.getLevel() != null ? course.getLevel().toString() : "BEGINNER"
                ));
            }
            // Sort như cũ (revenue DESC, reviewCount DESC, avgRating DESC, title ASC)
            result = result.stream()
                .sorted(Comparator.comparing(CourseAnalyticsDetailResponseDTO::getRevenue, Comparator.reverseOrder())
                    .thenComparing(CourseAnalyticsDetailResponseDTO::getReviews, Comparator.reverseOrder())
                    .thenComparing(CourseAnalyticsDetailResponseDTO::getRating, Comparator.reverseOrder())
                    .thenComparing(CourseAnalyticsDetailResponseDTO::getCourseName))
                .toList();
            // Phân trang thủ công
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), result.size());
            List<CourseAnalyticsDetailResponseDTO> paged = start >= result.size() ? Collections.emptyList() : result.subList(start, end);
            return new PageImpl<>(paged, pageable, result.size());
        } catch (Exception e) {
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
            List<CourseEntity> courses = courseRepository.findAll();
            List<StudentAnalyticsDetailResponseDTO> result = new ArrayList<>();
            // Tính previous period chuẩn
            long millisPerDay = 24 * 60 * 60 * 1000L;
            long numDays = (endDate.getTime() - startDate.getTime()) / millisPerDay + 1;
            Date previousEnd = new Date(startDate.getTime() - 1);
            Date previousStart = new Date(previousEnd.getTime() - (numDays - 1) * millisPerDay);
            for (CourseEntity course : courses) {
                Integer newStudents = analyticsRepository.countStudentsByCourseAndPeriod(course.getId(), startDate, endDate);
                Integer previousCompletion = analyticsRepository.countStudentsByCourseAndPeriod(course.getId(), previousStart, previousEnd);
                Double growth = 0.0;
                if (previousCompletion != null && previousCompletion > 0) {
                    growth = ((double)(newStudents != null ? newStudents : 0) - previousCompletion) / previousCompletion * 100.0;
                } else if (newStudents != null && newStudents > 0) {
                    growth = 100.0;
                }
                Integer reviews = analyticsRepository.countReviewsByCourseAndPeriod(course.getId(), startDate, endDate).intValue();
                Double avgRating = analyticsRepository.avgRatingByCourseAndPeriod(course.getId(), startDate, endDate);
                result.add(new StudentAnalyticsDetailResponseDTO(
                    course.getId(),
                    course.getTitle(),
                    newStudents != null ? newStudents : 0,
                    previousCompletion != null ? previousCompletion : 0,
                    Double.parseDouble(String.format("%.2f", growth)),
                    reviews != null ? reviews : 0,
                    avgRating != null ? Double.parseDouble(String.format("%.2f", avgRating)) : 0.0
                ));
            }
            // Sort và phân trang thủ công nếu cần
            result = result.stream()
                .sorted(Comparator.comparing(StudentAnalyticsDetailResponseDTO::getNewStudents, Comparator.reverseOrder())
                    .thenComparing(StudentAnalyticsDetailResponseDTO::getGrowth, Comparator.reverseOrder())
                    .thenComparing(StudentAnalyticsDetailResponseDTO::getReviews, Comparator.reverseOrder())
                    .thenComparing(StudentAnalyticsDetailResponseDTO::getAvgRating, Comparator.reverseOrder())
                    .thenComparing(StudentAnalyticsDetailResponseDTO::getCourseName))
                .toList();
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), result.size());
            List<StudentAnalyticsDetailResponseDTO> paged = start >= result.size() ? Collections.emptyList() : result.subList(start, end);
            return new PageImpl<>(paged, pageable, result.size());
        } catch (Exception e) {
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

    @Override
    @Transactional(readOnly = true)
    public Page<RevenueAnalyticsDetailResponseDTO> getRevenueAnalyticsDetails(
            Date startDate,
            Date endDate,
            Pageable pageable) throws AnalyticsRetrievalException {
        try {
            List<CourseEntity> courses = courseRepository.findAll();
            List<RevenueAnalyticsDetailResponseDTO> result = new ArrayList<>();
            // Tính previous period chuẩn
            long millisPerDay = 24 * 60 * 60 * 1000L;
            long numDays = (endDate.getTime() - startDate.getTime()) / millisPerDay + 1;
            Date previousEnd = new Date(startDate.getTime() - 1);
            Date previousStart = new Date(previousEnd.getTime() - (numDays - 1) * millisPerDay);
            Double totalRevenue = 0.0;
            for (CourseEntity course : courses) {
                Double revenue = analyticsRepository.sumRevenueByCourseAndPeriod(course.getId(), startDate, endDate);
                if (revenue != null) totalRevenue += revenue;
            }
            for (CourseEntity course : courses) {
                Double revenue = analyticsRepository.sumRevenueByCourseAndPeriod(course.getId(), startDate, endDate);
                Double previousRevenue = analyticsRepository.sumRevenueByCourseAndPeriod(course.getId(), previousStart, previousEnd);
                Double growth = 0.0;
                if (previousRevenue != null && previousRevenue > 0) {
                    growth = ((revenue != null ? revenue : 0.0) - previousRevenue) / previousRevenue * 100.0;
                } else if (revenue != null && revenue > 0) {
                    growth = 100.0;
                }
                Integer orders = 0; // Nếu có method đếm orders thì thay thế ở đây
                Integer newStudents = analyticsRepository.countStudentsByCourseAndPeriod(course.getId(), startDate, endDate);
                Double revenueShare = (totalRevenue > 0 && revenue != null) ? Double.parseDouble(String.format("%.2f", (revenue / totalRevenue) * 100)) : 0.0;
                result.add(new RevenueAnalyticsDetailResponseDTO(
                    course.getId(),
                    course.getTitle(),
                    revenue != null ? revenue : 0.0,
                    previousRevenue != null ? previousRevenue : 0.0,
                    Double.parseDouble(String.format("%.2f", growth)),
                    orders,
                    newStudents != null ? newStudents : 0,
                    revenueShare
                ));
            }
            // Sort và phân trang thủ công nếu cần
            result = result.stream()
                .sorted(Comparator.comparing(RevenueAnalyticsDetailResponseDTO::getRevenue, Comparator.reverseOrder())
                    .thenComparing(RevenueAnalyticsDetailResponseDTO::getGrowth, Comparator.reverseOrder())
                    .thenComparing(RevenueAnalyticsDetailResponseDTO::getOrders, Comparator.reverseOrder())
                    .thenComparing(RevenueAnalyticsDetailResponseDTO::getNewStudents, Comparator.reverseOrder())
                    .thenComparing(RevenueAnalyticsDetailResponseDTO::getRevenueShare, Comparator.reverseOrder())
                    .thenComparing(RevenueAnalyticsDetailResponseDTO::getCourseName))
                .toList();
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), result.size());
            List<RevenueAnalyticsDetailResponseDTO> paged = start >= result.size() ? Collections.emptyList() : result.subList(start, end);
            return new PageImpl<>(paged, pageable, result.size());
        } catch (Exception e) {
            throw new AnalyticsRetrievalException("Failed to retrieve revenue analytics details: " + e.getMessage(), e);
        }
    }

    /**
     * Tính tỷ lệ tăng trưởng revenue với format 2 chữ số thập phân
     * Formula: ((current - previous) / previous) * 100
     */
    private Double calculateRevenueGrowthRate(Double current, Double previous) {
        log.debug("Calculating revenue growth rate: current={}, previous={}", current, previous);
        
        // Handle null cases
        if (previous == null || previous == 0.0) {
            // Nếu không có revenue trước đó, mà hiện tại có => 100% growth
            Double result = current != null && current > 0.0 ? 100.0 : 0.0;
            log.debug("Previous revenue is null/zero, returning: {}", result);
            return result;
        }
        if (current == null) {
            log.debug("Current revenue is null, returning: -100.0");
            return -100.0;
        }
        
        // Calculate growth rate: ((current - previous) / previous) * 100
        double growth = ((current - previous) / previous) * 100.0;
        Double formattedGrowth = Double.parseDouble(String.format("%.2f", growth));
        
        log.debug("Revenue growth calculation: (({} - {}) / {}) * 100 = {}", 
                current, previous, previous, formattedGrowth);
        
        return formattedGrowth;
    }

    /**
     * Tính tỷ trọng doanh thu (revenue share) với format 2 chữ số thập phân
     * Formula: (course revenue / total revenue) * 100
     */
    private Double calculateRevenueShare(Double courseRevenue, Double totalRevenue) {
        log.debug("Calculating revenue share: courseRevenue={}, totalRevenue={}", courseRevenue, totalRevenue);
        
        if (totalRevenue == null || totalRevenue == 0.0) {
            log.debug("Total revenue is null/zero, returning: 0.0");
            return 0.0;
        }
        if (courseRevenue == null) {
            log.debug("Course revenue is null, returning: 0.0");
            return 0.0;
        }
        
        // Calculate revenue share: (course revenue / total revenue) * 100
        double share = (courseRevenue / totalRevenue) * 100.0;
        Double formattedShare = Double.parseDouble(String.format("%.2f", share));
        
        log.debug("Revenue share calculation: ({} / {}) * 100 = {}", 
                courseRevenue, totalRevenue, formattedShare);
        
        return formattedShare;
    }

}