package com.coursehub.service.impl;

import com.coursehub.dto.response.dashboard.DashboardManagerResponseDTO.TopCourse;
import com.coursehub.repository.CategoryRepository;
import com.coursehub.repository.CourseRepository;
import com.coursehub.repository.EnrollmentRepository;
import com.coursehub.repository.PaymentRepository;
import com.coursehub.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.Collections;
import com.coursehub.dto.response.dashboard.DashboardManagerResponseDTO;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final CategoryRepository categoryRepository;

    // Khai báo biến ngày đúng thứ tự, không lặp lại
    private final LocalDate today = LocalDate.now();
    private final LocalDate sameDayLastMonth = today.minusMonths(1);
    private final LocalDate sameDayTwoMonthsAgo = today.minusMonths(2);
    private final LocalDate sameDayThreeMonthsAgo = today.minusMonths(3);
    private final LocalDate sameDayLastYear = today.minusYears(1);
    private final LocalDate sameDayFourMonthsAgo = today.minusMonths(4);

    private final Date todayDate = Date.valueOf(today);
    private final Date sameDayLastMonthDate = Date.valueOf(sameDayLastMonth);
    private final Date sameDayTwoMonthsAgoDate = Date.valueOf(sameDayTwoMonthsAgo);
    private final Date sameDayThreeMonthsAgoDate = Date.valueOf(sameDayThreeMonthsAgo);
    private final Date sameDayLastYearDate = Date.valueOf(sameDayLastYear);
    private final Date sameDayFourMonthsAgoDate = Date.valueOf(sameDayFourMonthsAgo);

    @Override
    public Long getTotalCategories() {
        return categoryRepository.countCategoriesByCreatedAtBetween(sameDayLastMonthDate, todayDate);
    }

    @Override
    public Float getCategoryGrowth() {
        long thisMonth = categoryRepository.countCategoriesByCreatedAtBetween(sameDayLastMonthDate, todayDate);
        long lastMonth = categoryRepository.countCategoriesByCreatedAtBetween(sameDayTwoMonthsAgoDate, sameDayLastMonthDate);
        if (lastMonth == 0) return 0f;
        float growth = ((float) (thisMonth - lastMonth) / lastMonth) * 100;
        return Float.parseFloat(String.format("%.2f", growth));
    }

    @Override
    public Long getTotalCourses() {
        return courseRepository.countCoursesByCreatedAtBetween(sameDayLastMonthDate, todayDate);
    }

    @Override
    public Float getCourseGrowth() {
        long thisMonth = courseRepository.countCoursesByCreatedAtBetween(sameDayLastMonthDate, todayDate);
        long lastMonth = courseRepository.countCoursesByCreatedAtBetween(sameDayTwoMonthsAgoDate, sameDayLastMonthDate);
        if (lastMonth == 0) return 0f;
        float growth = ((float) (thisMonth - lastMonth) / lastMonth) * 100;
        return Float.parseFloat(String.format("%.2f", growth));
    }

    @Override
    public Long getTotalStudents() {
        return enrollmentRepository.countDistinctUserIdByCreatedAtBetween(sameDayLastMonthDate, todayDate);
    }

    @Override
    public Float getStudentGrowth() {
        long thisMonth = enrollmentRepository.countDistinctUserIdByCreatedAtBetween(sameDayLastMonthDate, todayDate);
        long lastMonth = enrollmentRepository.countDistinctUserIdByCreatedAtBetween(sameDayTwoMonthsAgoDate, sameDayLastMonthDate);
        if (lastMonth == 0) return 0f;
        float growth = ((float) (thisMonth - lastMonth) / lastMonth) * 100;
        return Float.parseFloat(String.format("%.2f", growth));
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return Optional.ofNullable(paymentRepository.sumCompletedPayments(sameDayLastMonthDate, todayDate))
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Float getRevenueGrowth() {
        // Tăng trưởng doanh thu tháng này so với tháng trước
        BigDecimal thisMonth = Optional.ofNullable(paymentRepository.sumCompletedPayments(sameDayLastMonthDate, todayDate)).orElse(BigDecimal.ZERO);
        BigDecimal lastMonth = Optional.ofNullable(paymentRepository.sumCompletedPayments(sameDayTwoMonthsAgoDate, sameDayLastMonthDate)).orElse(BigDecimal.ZERO);
        if (lastMonth.compareTo(BigDecimal.ZERO) == 0) return 0f;
        BigDecimal diff = thisMonth.subtract(lastMonth);
        BigDecimal percent = diff.divide(lastMonth, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return percent.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    @Override
    public BigDecimal getTotalLastMonthRevenue() {
        // Tổng doanh thu từ ngày này tháng trước trước đến ngày này tháng trước
        BigDecimal total = Optional.ofNullable(paymentRepository.sumCompletedPayments(sameDayTwoMonthsAgoDate, sameDayLastMonthDate)).orElse(BigDecimal.ZERO);
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Float getLastMonthRevenueGrowth() {
        // Tăng trưởng doanh thu tháng trước so với tháng trước nữa
        BigDecimal lastMonth = Optional.ofNullable(paymentRepository.sumCompletedPayments(sameDayTwoMonthsAgoDate, sameDayLastMonthDate)).orElse(BigDecimal.ZERO);
        BigDecimal prevMonth = Optional.ofNullable(paymentRepository.sumCompletedPayments(sameDayThreeMonthsAgoDate, sameDayTwoMonthsAgoDate)).orElse(BigDecimal.ZERO);
        if (prevMonth.compareTo(BigDecimal.ZERO) == 0) return 0f;
        BigDecimal diff = lastMonth.subtract(prevMonth);
        BigDecimal percent = diff.divide(prevMonth, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return percent.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    @Override
    public BigDecimal getTotalThreeMonthsAgoRevenue() {
        // Tổng doanh thu từ ngày này 3 tháng trước đến ngày này 2 tháng trước
        BigDecimal total = Optional.ofNullable(paymentRepository.sumCompletedPayments(sameDayThreeMonthsAgoDate, sameDayTwoMonthsAgoDate)).orElse(BigDecimal.ZERO);
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Float getThreeMonthsAgoRevenueGrowth() {
        // Tăng trưởng doanh thu 3 tháng trước so với 4 tháng trước
        BigDecimal threeMonthsAgo = Optional.ofNullable(paymentRepository.sumCompletedPayments(sameDayThreeMonthsAgoDate, sameDayTwoMonthsAgoDate)).orElse(BigDecimal.ZERO);
        BigDecimal fourMonthsAgo = Optional.ofNullable(paymentRepository.sumCompletedPayments(sameDayFourMonthsAgoDate, sameDayThreeMonthsAgoDate)).orElse(BigDecimal.ZERO);
        if (fourMonthsAgo.compareTo(BigDecimal.ZERO) == 0) return 0f;
        BigDecimal diff = threeMonthsAgo.subtract(fourMonthsAgo);
        BigDecimal percent = diff.divide(fourMonthsAgo, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return percent.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    @Override
    public List<Double> getMonthlyRevenue() {
        return Collections.emptyList();
    }

    @Override
    public List<Long> getMonthlyNewCourses() {
        return Collections.emptyList();
    }

    @Override
    public List<TopCourse> getTopCourses() {
        return Collections.emptyList();
    }

    @Override
    public DashboardManagerResponseDTO getManagerDashboard() {
        return DashboardManagerResponseDTO.builder()
                .totalCategories(getTotalCategories())
                .categoryGrowth(getCategoryGrowth())
                .totalCourses(getTotalCourses())
                .courseGrowth(getCourseGrowth())
                .totalStudents(getTotalStudents())
                .studentGrowth(getStudentGrowth())
                .totalRevenue(getTotalRevenue())
                .revenueGrowth(getRevenueGrowth())
                .totalLastMonthRevenue(getTotalLastMonthRevenue())
                .lastMonthRevenueGrowth(getLastMonthRevenueGrowth())
                .totalThreeMonthsAgoRevenue(getTotalThreeMonthsAgoRevenue())
                .threeMonthsAgoRevenueGrowth(getThreeMonthsAgoRevenueGrowth())
                .monthlyRevenue(getMonthlyRevenue())
                .monthlyNewCourses(getMonthlyNewCourses())
                .topCourses(getTopCourses())
                .build();
    }
}
