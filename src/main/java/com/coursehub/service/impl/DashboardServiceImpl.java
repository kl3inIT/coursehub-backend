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
import com.coursehub.dto.response.dashboard.DashboardManagerResponseDTO;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final CategoryRepository categoryRepository;

    // Chỉ giữ lại biến today, xoá các biến ngày/tháng không còn dùng
    private final LocalDate today = LocalDate.now();

    @Override
    public Long getTotalCategories() {
        LocalDate firstDayThisMonth = today.withDayOfMonth(1);
        LocalDate lastDayThisMonth = today.withDayOfMonth(today.lengthOfMonth());
        Date start = Date.valueOf(firstDayThisMonth);
        Date end = Date.valueOf(lastDayThisMonth);
        return categoryRepository.countCategoriesByCreatedAtBetween(start, end);
    }

    @Override
    public Float getCategoryGrowth() {
        LocalDate firstDayThisMonth = today.withDayOfMonth(1);
        LocalDate lastDayThisMonth = today.withDayOfMonth(today.lengthOfMonth());
        LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);
        LocalDate lastDayLastMonth = firstDayLastMonth.withDayOfMonth(firstDayLastMonth.lengthOfMonth());
        Date startThis = Date.valueOf(firstDayThisMonth);
        Date endThis = Date.valueOf(lastDayThisMonth);
        Date startLast = Date.valueOf(firstDayLastMonth);
        Date endLast = Date.valueOf(lastDayLastMonth);
        long thisMonth = categoryRepository.countCategoriesByCreatedAtBetween(startThis, endThis);
        long lastMonth = categoryRepository.countCategoriesByCreatedAtBetween(startLast, endLast);
        if (lastMonth == 0) return 0f;
        float growth = ((float) (thisMonth - lastMonth) / lastMonth) * 100;
        return Float.parseFloat(String.format("%.2f", growth));
    }

    @Override
    public Long getTotalCourses() {
        LocalDate firstDayThisMonth = today.withDayOfMonth(1);
        LocalDate lastDayThisMonth = today.withDayOfMonth(today.lengthOfMonth());
        Date start = Date.valueOf(firstDayThisMonth);
        Date end = Date.valueOf(lastDayThisMonth);
        return courseRepository.countCoursesByCreatedAtBetween(start, end);
    }

    @Override
    public Float getCourseGrowth() {
        LocalDate firstDayThisMonth = today.withDayOfMonth(1);
        LocalDate lastDayThisMonth = today.withDayOfMonth(today.lengthOfMonth());
        LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);
        LocalDate lastDayLastMonth = firstDayLastMonth.withDayOfMonth(firstDayLastMonth.lengthOfMonth());
        Date startThis = Date.valueOf(firstDayThisMonth);
        Date endThis = Date.valueOf(lastDayThisMonth);
        Date startLast = Date.valueOf(firstDayLastMonth);
        Date endLast = Date.valueOf(lastDayLastMonth);
        long thisMonth = courseRepository.countCoursesByCreatedAtBetween(startThis, endThis);
        long lastMonth = courseRepository.countCoursesByCreatedAtBetween(startLast, endLast);
        if (lastMonth == 0) return 0f;
        float growth = ((float) (thisMonth - lastMonth) / lastMonth) * 100;
        return Float.parseFloat(String.format("%.2f", growth));
    }

    @Override
    public Long getTotalStudents() {
        LocalDate firstDayThisMonth = today.withDayOfMonth(1);
        LocalDate lastDayThisMonth = today.withDayOfMonth(today.lengthOfMonth());
        Date start = Date.valueOf(firstDayThisMonth);
        Date end = Date.valueOf(lastDayThisMonth);
        return enrollmentRepository.countDistinctUserIdByCreatedAtBetween(start, end);
    }

    @Override
    public Float getStudentGrowth() {
        LocalDate firstDayThisMonth = today.withDayOfMonth(1);
        LocalDate lastDayThisMonth = today.withDayOfMonth(today.lengthOfMonth());
        LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);
        LocalDate lastDayLastMonth = firstDayLastMonth.withDayOfMonth(firstDayLastMonth.lengthOfMonth());
        Date startThis = Date.valueOf(firstDayThisMonth);
        Date endThis = Date.valueOf(lastDayThisMonth);
        Date startLast = Date.valueOf(firstDayLastMonth);
        Date endLast = Date.valueOf(lastDayLastMonth);
        long thisMonth = enrollmentRepository.countDistinctUserIdByCreatedAtBetween(startThis, endThis);
        long lastMonth = enrollmentRepository.countDistinctUserIdByCreatedAtBetween(startLast, endLast);
        if (lastMonth == 0) return 0f;
        float growth = ((float) (thisMonth - lastMonth) / lastMonth) * 100;
        return Float.parseFloat(String.format("%.2f", growth));
    }

    @Override
    public BigDecimal getTotalRevenue() {
        LocalDate firstDayThisMonth = today.withDayOfMonth(1);
        LocalDate lastDayThisMonth = today.withDayOfMonth(today.lengthOfMonth());
        Date start = Date.valueOf(firstDayThisMonth);
        Date end = Date.valueOf(lastDayThisMonth);
        return Optional.ofNullable(paymentRepository.sumCompletedPayments(start, end))
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Float getRevenueGrowth() {
        LocalDate firstDayThisMonth = today.withDayOfMonth(1);
        LocalDate lastDayThisMonth = today.withDayOfMonth(today.lengthOfMonth());
        LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);
        LocalDate lastDayLastMonth = firstDayLastMonth.withDayOfMonth(firstDayLastMonth.lengthOfMonth());
        Date startThis = Date.valueOf(firstDayThisMonth);
        Date endThis = Date.valueOf(lastDayThisMonth);
        Date startLast = Date.valueOf(firstDayLastMonth);
        Date endLast = Date.valueOf(lastDayLastMonth);
        BigDecimal thisMonth = Optional.ofNullable(paymentRepository.sumCompletedPayments(startThis, endThis)).orElse(BigDecimal.ZERO);
        BigDecimal lastMonth = Optional.ofNullable(paymentRepository.sumCompletedPayments(startLast, endLast)).orElse(BigDecimal.ZERO);
        if (lastMonth.compareTo(BigDecimal.ZERO) == 0) return 0f;
        BigDecimal diff = thisMonth.subtract(lastMonth);
        BigDecimal percent = diff.divide(lastMonth, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return percent.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    @Override
    public BigDecimal getTotalLastMonthRevenue() {
        // Tổng doanh thu tháng trước (từ ngày 1 đến cuối tháng trước)
        LocalDate firstDayLastMonth = today.withDayOfMonth(1).minusMonths(1);
        LocalDate lastDayLastMonth = firstDayLastMonth.withDayOfMonth(firstDayLastMonth.lengthOfMonth());
        Date start = Date.valueOf(firstDayLastMonth);
        Date end = Date.valueOf(lastDayLastMonth);
        BigDecimal total = Optional.ofNullable(paymentRepository.sumCompletedPayments(start, end)).orElse(BigDecimal.ZERO);
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Float getLastMonthRevenueGrowth() {
        // Tăng trưởng doanh thu tháng trước so với tháng trước nữa
        LocalDate firstDayLastMonth = today.withDayOfMonth(1).minusMonths(1);
        LocalDate lastDayLastMonth = firstDayLastMonth.withDayOfMonth(firstDayLastMonth.lengthOfMonth());
        LocalDate firstDayPrevMonth = firstDayLastMonth.minusMonths(1);
        LocalDate lastDayPrevMonth = firstDayPrevMonth.withDayOfMonth(firstDayPrevMonth.lengthOfMonth());
        Date startLast = Date.valueOf(firstDayLastMonth);
        Date endLast = Date.valueOf(lastDayLastMonth);
        Date startPrev = Date.valueOf(firstDayPrevMonth);
        Date endPrev = Date.valueOf(lastDayPrevMonth);
        BigDecimal lastMonth = Optional.ofNullable(paymentRepository.sumCompletedPayments(startLast, endLast)).orElse(BigDecimal.ZERO);
        BigDecimal prevMonth = Optional.ofNullable(paymentRepository.sumCompletedPayments(startPrev, endPrev)).orElse(BigDecimal.ZERO);
        if (prevMonth.compareTo(BigDecimal.ZERO) == 0) return 0f;
        BigDecimal diff = lastMonth.subtract(prevMonth);
        BigDecimal percent = diff.divide(prevMonth, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return percent.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    @Override
    public BigDecimal getTotalThreeMonthsAgoRevenue() {
        // Tổng doanh thu 3 tháng trước (từ ngày 1 đến cuối tháng 3 tháng trước)
        LocalDate firstDayThreeMonthsAgo = today.withDayOfMonth(1).minusMonths(2);
        LocalDate lastDayThreeMonthsAgo = firstDayThreeMonthsAgo.withDayOfMonth(firstDayThreeMonthsAgo.lengthOfMonth());
        Date start = Date.valueOf(firstDayThreeMonthsAgo);
        Date end = Date.valueOf(lastDayThreeMonthsAgo);
        BigDecimal total = Optional.ofNullable(paymentRepository.sumCompletedPayments(start, end)).orElse(BigDecimal.ZERO);
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Float getThreeMonthsAgoRevenueGrowth() {
        // Tăng trưởng doanh thu 3 tháng trước so với 4 tháng trước
        LocalDate firstDayThreeMonthsAgo = today.withDayOfMonth(1).minusMonths(2);
        LocalDate lastDayThreeMonthsAgo = firstDayThreeMonthsAgo.withDayOfMonth(firstDayThreeMonthsAgo.lengthOfMonth());
        LocalDate firstDayFourMonthsAgo = firstDayThreeMonthsAgo.minusMonths(1);
        LocalDate lastDayFourMonthsAgo = firstDayFourMonthsAgo.withDayOfMonth(firstDayFourMonthsAgo.lengthOfMonth());
        Date startThree = Date.valueOf(firstDayThreeMonthsAgo);
        Date endThree = Date.valueOf(lastDayThreeMonthsAgo);
        Date startFour = Date.valueOf(firstDayFourMonthsAgo);
        Date endFour = Date.valueOf(lastDayFourMonthsAgo);
        BigDecimal threeMonthsAgo = Optional.ofNullable(paymentRepository.sumCompletedPayments(startThree, endThree)).orElse(BigDecimal.ZERO);
        BigDecimal fourMonthsAgo = Optional.ofNullable(paymentRepository.sumCompletedPayments(startFour, endFour)).orElse(BigDecimal.ZERO);
        if (fourMonthsAgo.compareTo(BigDecimal.ZERO) == 0) return 0f;
        BigDecimal diff = threeMonthsAgo.subtract(fourMonthsAgo);
        BigDecimal percent = diff.divide(fourMonthsAgo, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return percent.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    @Override
    public List<Double> getMonthlyRevenue() {
        int year = LocalDate.now().getYear();
        List<Object[]> results = paymentRepository.sumRevenueByMonth(year);
        Double[] monthly = new Double[12];
        Arrays.fill(monthly, 0.0);
        for (Object[] row : results) {
            int month = ((Integer) row[0]) - 1;
            Double total = ((Number) row[1]).doubleValue();
            monthly[month] = total;
        }
        return Arrays.asList(monthly);
    }

    @Override
    public List<Long> getMonthlyNewCourses() {
        int year = LocalDate.now().getYear();
        List<Object[]> results = courseRepository.countNewCoursesByMonth(year);
        Long[] monthly = new Long[12];
        Arrays.fill(monthly, 0L);
        for (Object[] row : results) {
            int month = ((Integer) row[0]) - 1;
            Long total = ((Number) row[1]).longValue();
            monthly[month] = total;
        }
        return Arrays.asList(monthly);
    }

    @Override
    public List<Long> getMonthlyStudentEnrollments() {
        int year = LocalDate.now().getYear();
        List<Object[]> results = enrollmentRepository.countStudentEnrollmentsByMonth(year);
        Long[] monthly = new Long[12];
        Arrays.fill(monthly, 0L);
        for (Object[] row : results) {
            int month = ((Integer) row[0]) - 1;
            Long total = ((Number) row[1]).longValue();
            monthly[month] = total;
        }
        return Arrays.asList(monthly);
    }

    @Override
    public List<TopCourse> getTopCourses() {
        int year = LocalDate.now().getYear();
        List<Object[]> results = enrollmentRepository.findTopCoursesByEnrollments(year);
        List<TopCourse> topCourses = new ArrayList<>();
        for (Object[] row : results) {
            String name = (String) row[1];
            Long students = ((Number) row[2]).longValue();
            topCourses.add(new TopCourse(name, students));
        }
        // Lấy top 3 nếu có nhiều hơn 3
        return topCourses.size() > 3 ? topCourses.subList(0, 3) : topCourses;
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
                .monthlyStudentEnrollments(getMonthlyStudentEnrollments())
                .topCourses(getTopCourses())
                .build();
    }

    @Override
    public DashboardManagerResponseDTO getManagerDashboardStats(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        Date startDate = Date.valueOf(start);
        Date endDate = Date.valueOf(end);

        // Tháng trước
        LocalDate prevMonthStart = start.minusMonths(1).withDayOfMonth(1);
        LocalDate prevMonthEnd = prevMonthStart.withDayOfMonth(prevMonthStart.lengthOfMonth());
        Date prevStartDate = Date.valueOf(prevMonthStart);
        Date prevEndDate = Date.valueOf(prevMonthEnd);

        // Categories
        long totalCategories = categoryRepository.countCategoriesByCreatedAtBetween(startDate, endDate);
        long prevCategories = categoryRepository.countCategoriesByCreatedAtBetween(prevStartDate, prevEndDate);
        float categoryGrowth = (prevCategories == 0) ? 0f : ((float)(totalCategories - prevCategories) / prevCategories) * 100;
        categoryGrowth = Float.parseFloat(String.format("%.2f", categoryGrowth));

        // Courses
        long totalCourses = courseRepository.countCoursesByCreatedAtBetween(startDate, endDate);
        long prevCourses = courseRepository.countCoursesByCreatedAtBetween(prevStartDate, prevEndDate);
        float courseGrowth = (prevCourses == 0) ? 0f : ((float)(totalCourses - prevCourses) / prevCourses) * 100;
        courseGrowth = Float.parseFloat(String.format("%.2f", courseGrowth));

        // Students
        long totalStudents = enrollmentRepository.countDistinctUserIdByCreatedAtBetween(startDate, endDate);
        long prevStudents = enrollmentRepository.countDistinctUserIdByCreatedAtBetween(prevStartDate, prevEndDate);
        float studentGrowth = (prevStudents == 0) ? 0f : ((float)(totalStudents - prevStudents) / prevStudents) * 100;
        studentGrowth = Float.parseFloat(String.format("%.2f", studentGrowth));

        // Revenue
        BigDecimal totalRevenue = paymentRepository.sumCompletedPayments(startDate, endDate);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        BigDecimal prevRevenue = paymentRepository.sumCompletedPayments(prevStartDate, prevEndDate);
        if (prevRevenue == null) prevRevenue = BigDecimal.ZERO;
        float revenueGrowth = (prevRevenue.compareTo(BigDecimal.ZERO) == 0) ? 0f : totalRevenue.subtract(prevRevenue).divide(prevRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).floatValue();

        return DashboardManagerResponseDTO.builder()
                .totalCategories(totalCategories)
                .categoryGrowth(categoryGrowth)
                .totalCourses(totalCourses)
                .courseGrowth(courseGrowth)
                .totalStudents(totalStudents)
                .studentGrowth(studentGrowth)
                .totalRevenue(totalRevenue.setScale(2, RoundingMode.HALF_UP))
                .revenueGrowth(revenueGrowth)
                .build();
    }

    @Override
    public List<Double> getMonthlyRevenueByYear(int year) {
        List<Object[]> results = paymentRepository.sumRevenueByMonth(year);
        Double[] monthly = new Double[12];
        Arrays.fill(monthly, 0.0);
        for (Object[] row : results) {
            int month = ((Integer) row[0]) - 1;
            Double total = ((Number) row[1]).doubleValue();
            monthly[month] = total;
        }
        return Arrays.asList(monthly);
    }

    @Override
    public List<Long> getMonthlyNewCoursesByYear(int year) {
        List<Object[]> results = courseRepository.countNewCoursesByMonth(year);
        Long[] monthly = new Long[12];
        Arrays.fill(monthly, 0L);
        for (Object[] row : results) {
            int month = ((Integer) row[0]) - 1;
            Long total = ((Number) row[1]).longValue();
            monthly[month] = total;
        }
        return Arrays.asList(monthly);
    }

    @Override
    public List<Long> getMonthlyStudentEnrollmentsByYear(int year) {
        List<Object[]> results = enrollmentRepository.countStudentEnrollmentsByMonth(year);
        Long[] monthly = new Long[12];
        Arrays.fill(monthly, 0L);
        for (Object[] row : results) {
            int month = ((Integer) row[0]) - 1;
            Long total = ((Number) row[1]).longValue();
            monthly[month] = total;
        }
        return Arrays.asList(monthly);
    }

    @Override
    public List<TopCourse> getTopCoursesByMonthYear(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        Date startDate = Date.valueOf(start);
        Date endDate = Date.valueOf(end);
        List<Object[]> results = enrollmentRepository.findTopCoursesByEnrollmentsAndCreatedAtBetween(startDate, endDate);
        List<TopCourse> topCourses = new ArrayList<>();
        for (Object[] row : results) {
            String name = (String) row[1];
            Long students = ((Number) row[2]).longValue();
            topCourses.add(new TopCourse(name, students));
        }
        return topCourses.size() > 3 ? topCourses.subList(0, 3) : topCourses;
    }

    @Override
    public DashboardManagerResponseDTO getRevenueInsightsByMonthYear(int month, int year) {
        // Tính toán doanh thu tháng hiện tại, tháng trước, 3 tháng trước và tăng trưởng tương ứng
        LocalDate thisMonthStart = LocalDate.of(year, month, 1);
        LocalDate thisMonthEnd = thisMonthStart.withDayOfMonth(thisMonthStart.lengthOfMonth());
        Date thisStart = Date.valueOf(thisMonthStart);
        Date thisEnd = Date.valueOf(thisMonthEnd);

        LocalDate lastMonthStart = thisMonthStart.minusMonths(1);
        LocalDate lastMonthEnd = lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth());
        Date lastStart = Date.valueOf(lastMonthStart);
        Date lastEnd = Date.valueOf(lastMonthEnd);

        LocalDate threeMonthsAgoStart = thisMonthStart.minusMonths(3);
        LocalDate threeMonthsAgoEnd = threeMonthsAgoStart.withDayOfMonth(threeMonthsAgoStart.lengthOfMonth());
        Date threeStart = Date.valueOf(threeMonthsAgoStart);
        Date threeEnd = Date.valueOf(threeMonthsAgoEnd);

        BigDecimal thisMonthRevenue = Optional.ofNullable(paymentRepository.sumCompletedPayments(thisStart, thisEnd)).orElse(BigDecimal.ZERO);
        BigDecimal lastMonthRevenue = Optional.ofNullable(paymentRepository.sumCompletedPayments(lastStart, lastEnd)).orElse(BigDecimal.ZERO);
        BigDecimal threeMonthsAgoRevenue = Optional.ofNullable(paymentRepository.sumCompletedPayments(threeStart, threeEnd)).orElse(BigDecimal.ZERO);

        float revenueGrowth = (lastMonthRevenue.compareTo(BigDecimal.ZERO) == 0) ? 0f : thisMonthRevenue.subtract(lastMonthRevenue).divide(lastMonthRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).floatValue();
        float lastMonthRevenueGrowth = (threeMonthsAgoRevenue.compareTo(BigDecimal.ZERO) == 0) ? 0f : lastMonthRevenue.subtract(threeMonthsAgoRevenue).divide(threeMonthsAgoRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).floatValue();
        float threeMonthsAgoRevenueGrowth = 0f; // Không so sánh xa hơn

        return DashboardManagerResponseDTO.builder()
                .totalRevenue(thisMonthRevenue.setScale(2, RoundingMode.HALF_UP))
                .revenueGrowth(revenueGrowth)
                .totalLastMonthRevenue(lastMonthRevenue.setScale(2, RoundingMode.HALF_UP))
                .lastMonthRevenueGrowth(lastMonthRevenueGrowth)
                .totalThreeMonthsAgoRevenue(threeMonthsAgoRevenue.setScale(2, RoundingMode.HALF_UP))
                .threeMonthsAgoRevenueGrowth(threeMonthsAgoRevenueGrowth)
                .build();
    }
}
