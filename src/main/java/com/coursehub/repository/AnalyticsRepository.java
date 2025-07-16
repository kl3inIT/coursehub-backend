package com.coursehub.repository;

import com.coursehub.dto.response.analytics.CategoryAnalyticsDetailResponseDTO;
import com.coursehub.dto.response.analytics.CourseAnalyticsDetailResponseDTO;
import com.coursehub.dto.response.analytics.StudentAnalyticsDetailResponseDTO;
import com.coursehub.entity.CategoryEntity;
import com.coursehub.entity.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface AnalyticsRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("SELECT new com.coursehub.dto.response.analytics.CategoryAnalyticsDetailResponseDTO(\n" +
            "    c.id, \n" +
            "    c.name, \n" +
            "    c.description, \n" +
            "    COUNT(DISTINCT CASE WHEN (:startDate IS NULL OR co.createdDate >= :startDate) AND (:endDate IS NULL OR co.createdDate < :endDate) THEN co.id ELSE NULL END), \n" +
            "    COUNT(DISTINCT CASE WHEN (:startDate IS NULL OR p.modifiedDate >= :startDate) AND (:endDate IS NULL OR p.modifiedDate < :endDate) THEN p.userEntity.id ELSE NULL END), \n" +
            "    COALESCE(SUM(CASE WHEN p.status = 'COMPLETED' AND (:startDate IS NULL OR p.modifiedDate >= :startDate) AND (:endDate IS NULL OR p.modifiedDate < :endDate) THEN p.amount ELSE 0.0 END), 0.0), \n" +
            "    0.0, \n" +
            "    c.createdDate, \n" +
            "    c.modifiedDate\n" +
            ") \n" +
            "FROM CategoryEntity c \n" +
            "LEFT JOIN c.courseEntities co \n" +
            "LEFT JOIN co.paymentEntities p \n" +
            "GROUP BY c.id, c.name, c.description, c.createdDate, c.modifiedDate \n" +
            "ORDER BY COALESCE(SUM(CASE WHEN p.status = 'COMPLETED' AND (:startDate IS NULL OR p.modifiedDate >= :startDate) AND (:endDate IS NULL OR p.modifiedDate < :endDate) THEN p.amount ELSE 0.0 END), 0.0) DESC, c.id ASC")
    Page<CategoryAnalyticsDetailResponseDTO> getCategoryAnalyticsDetails(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(CASE WHEN p.status = 'COMPLETED' THEN p.amount ELSE 0.0 END), 0.0) " +
            "FROM PaymentEntity p " +
            "WHERE (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
            "AND (:endDate IS NULL OR p.modifiedDate < :endDate)")
    Double getTotalRevenue(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // Course Analytics Methods - Tách thành nhiều query riêng biệt cho dễ hiểu

    /**
     * Lấy danh sách course analytics với phân trang và lọc theo thời gian
     */
    @Query("SELECT new com.coursehub.dto.response.analytics.CourseAnalyticsDetailResponseDTO(\n" +
            "    c.id, \n" +
            "    c.title, \n" +
            "    CAST(COALESCE(\n" +
            "        (SELECT COUNT(DISTINCT p1.userEntity.id) \n" +
            "         FROM PaymentEntity p1 \n" +
            "         WHERE p1.courseEntity.id = c.id \n" +
            "         AND p1.status = 'COMPLETED' \n" +
            "         AND (:startDate IS NULL OR p1.modifiedDate >= :startDate) \n" +
            "         AND (:endDate IS NULL OR p1.modifiedDate < :endDate)), 0\n" +
            "    ) AS int), \n" +
            "    COALESCE(\n" +
            "        (SELECT AVG(CAST(r1.star AS double)) \n" +
            "         FROM ReviewEntity r1 \n" +
            "         WHERE r1.courseEntity.id = c.id \n" +
            "         AND (:startDate IS NULL OR r1.createdDate >= :startDate) \n" +
            "         AND (:endDate IS NULL OR r1.createdDate < :endDate)), 0.0\n" +
            "    ), \n" +
            "    COALESCE(\n" +
            "        (SELECT SUM(p2.amount) \n" +
            "         FROM PaymentEntity p2 \n" +
            "         WHERE p2.courseEntity.id = c.id \n" +
            "         AND p2.status = 'COMPLETED' \n" +
            "         AND (:startDate IS NULL OR p2.modifiedDate >= :startDate) \n" +
            "         AND (:endDate IS NULL OR p2.modifiedDate < :endDate)), 0.0\n" +
            "    ), \n" +
            "    0.0, \n" +
            "    COALESCE(\n" +
            "        (SELECT COUNT(r2.id) \n" +
            "         FROM ReviewEntity r2 \n" +
            "         WHERE r2.courseEntity.id = c.id \n" +
            "         AND (:startDate IS NULL OR r2.createdDate >= :startDate) \n" +
            "         AND (:endDate IS NULL OR r2.createdDate < :endDate)), 0L\n" +
            "    ), \n" +
            "    CAST(COALESCE(c.level, 'BEGINNER') AS string)\n" +
            ") \n" +
            "FROM CourseEntity c \n" +
            "ORDER BY (\n" +
            "    COALESCE(\n" +
            "        (SELECT SUM(p2.amount) \n" +
            "         FROM PaymentEntity p2 \n" +
            "         WHERE p2.courseEntity.id = c.id \n" +
            "         AND p2.status = 'COMPLETED' \n" +
            "         AND (:startDate IS NULL OR p2.modifiedDate >= :startDate) \n" +
            "         AND (:endDate IS NULL OR p2.modifiedDate < :endDate)), 0.0\n" +
            "    )\n" +
            ") DESC, (\n" +
            "    COALESCE(\n" +
            "        (SELECT COUNT(r3.id) \n" +
            "         FROM ReviewEntity r3 \n" +
            "         WHERE r3.courseEntity.id = c.id \n" +
            "         AND (:startDate IS NULL OR r3.createdDate >= :startDate) \n" +
            "         AND (:endDate IS NULL OR r3.createdDate < :endDate)), 0L\n" +
            "    )\n" +
            ") DESC, (\n" +
            "    COALESCE(\n" +
            "        (SELECT AVG(CAST(r4.star AS double)) \n" +
            "         FROM ReviewEntity r4 \n" +
            "         WHERE r4.courseEntity.id = c.id \n" +
            "         AND (:startDate IS NULL OR r4.createdDate >= :startDate) \n" +
            "         AND (:endDate IS NULL OR r4.createdDate < :endDate)), 0.0\n" +
            "    )\n" +
            ") DESC, c.title ASC")
    Page<CourseAnalyticsDetailResponseDTO> getCourseAnalyticsDetails(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );

    /**
     * Tính tổng revenue của tất cả course trong khoảng thời gian để tính revenuePercent
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0.0) " +
            "FROM PaymentEntity p " +
            "WHERE p.status = 'COMPLETED' " +
            "AND (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
            "AND (:endDate IS NULL OR p.modifiedDate < :endDate)")
    Double getTotalCourseRevenue(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * Lấy số sinh viên của course cụ thể (distinct userID từ payment đã hoàn thành)
     */
    @Query("SELECT COUNT(DISTINCT p.userEntity.id) " +
            "FROM PaymentEntity p " +
            "WHERE p.courseEntity.id = :courseId " +
            "AND p.status = 'COMPLETED' " +
            "AND (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
            "AND (:endDate IS NULL OR p.modifiedDate < :endDate)")
    Integer getStudentCountByCourse(@Param("courseId") Long courseId, 
                                   @Param("startDate") Date startDate, 
                                   @Param("endDate") Date endDate);

    /**
     * Tính rating trung bình của course theo thời gian
     */
    @Query("SELECT COALESCE(AVG(CAST(r.star AS double)), 0.0) " +
            "FROM ReviewEntity r " +
            "WHERE r.courseEntity.id = :courseId " +
            "AND (:startDate IS NULL OR r.createdDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.createdDate < :endDate)")
    Double getAverageRatingByCourse(@Param("courseId") Long courseId,
                                   @Param("startDate") Date startDate,
                                   @Param("endDate") Date endDate);

    /**
     * Tính tổng revenue của course cụ thể theo thời gian
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0.0) " +
            "FROM PaymentEntity p " +
            "WHERE p.courseEntity.id = :courseId " +
            "AND p.status = 'COMPLETED' " +
            "AND (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
            "AND (:endDate IS NULL OR p.modifiedDate < :endDate)")
    Double getRevenueByCourse(@Param("courseId") Long courseId,
                             @Param("startDate") Date startDate,
                             @Param("endDate") Date endDate);

    /**
     * Đếm số review của course theo thời gian
     */
    @Query("SELECT COUNT(r.id) " +
            "FROM ReviewEntity r " +
            "WHERE r.courseEntity.id = :courseId " +
            "AND (:startDate IS NULL OR r.createdDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.createdDate < :endDate)")
    Long getReviewCountByCourse(@Param("courseId") Long courseId,
                               @Param("startDate") Date startDate,
                               @Param("endDate") Date endDate);

    /**
     * Lấy danh sách course để tính student analytics với phân trang
     * Note: Sorting sẽ được handle trong service layer theo thứ tự:
     * 1. New Students DESC
     * 2. Growth DESC (nếu new students bằng nhau)
     * 3. Reviews DESC (nếu growth bằng nhau)  
     * 4. Avg Rating DESC (nếu reviews bằng nhau)
     * 5. Course Name ASC (cuối cùng)
     */
    @Query("SELECT c FROM CourseEntity c ORDER BY c.title ASC")
    Page<CourseEntity> getAllCoursesForStudentAnalytics(Pageable pageable);

    /**
     * Đếm số sinh viên mới trong khoảng thời gian hiện tại (distinct userID từ payment đã hoàn thành)
     */
    @Query("SELECT COUNT(DISTINCT p.userEntity.id) " +
            "FROM PaymentEntity p " +
            "WHERE p.courseEntity.id = :courseId " +
            "AND p.status = 'COMPLETED' " +
            "AND (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
            "AND (:endDate IS NULL OR p.modifiedDate < :endDate)")
    Integer getNewStudentsByCourse(@Param("courseId") Long courseId,
                                   @Param("startDate") Date startDate,
                                   @Param("endDate") Date endDate);

    /**
     * Đếm số sinh viên trong khoảng thời gian trước đó (để tính growth rate)
     */
    @Query("SELECT COUNT(DISTINCT p.userEntity.id) " +
            "FROM PaymentEntity p " +
            "WHERE p.courseEntity.id = :courseId " +
            "AND p.status = 'COMPLETED' " +
            "AND (:previousStartDate IS NULL OR p.modifiedDate >= :previousStartDate) " +
            "AND (:previousEndDate IS NULL OR p.modifiedDate < :previousEndDate)")
    Integer getPreviousStudentsByCourse(@Param("courseId") Long courseId,
                                       @Param("previousStartDate") Date previousStartDate,
                                       @Param("previousEndDate") Date previousEndDate);

    /**
     * Đếm số review của course trong khoảng thời gian hiện tại
     */
    @Query("SELECT COUNT(r.id) " +
            "FROM ReviewEntity r " +
            "WHERE r.courseEntity.id = :courseId " +
            "AND (:startDate IS NULL OR r.createdDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.createdDate < :endDate) " +
            "AND r.star > 0")
    Integer getReviewsCountByCourse(@Param("courseId") Long courseId,
                                   @Param("startDate") Date startDate,
                                   @Param("endDate") Date endDate);

    /**
     * Tính điểm đánh giá trung bình của course trong khoảng thời gian hiện tại
     */
    @Query("SELECT COALESCE(AVG(CAST(r.star AS double)), 0.0) " +
            "FROM ReviewEntity r " +
            "WHERE r.courseEntity.id = :courseId " +
            "AND (:startDate IS NULL OR r.createdDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.createdDate < :endDate) " +
            "AND r.star > 0")
    Double getAvgRatingByCourse(@Param("courseId") Long courseId,
                               @Param("startDate") Date startDate,
                               @Param("endDate") Date endDate);

    // ==================== REVENUE ANALYTICS METHODS ====================

    /**
     * Lấy danh sách course để tính revenue analytics với phân trang
     * Note: Sorting sẽ được handle trong service layer theo thứ tự:
     * 1. Revenue DESC
     * 2. Growth DESC (nếu revenue bằng nhau)
     * 3. Orders DESC (nếu growth bằng nhau)
     * 4. New Students DESC (nếu orders bằng nhau)
     * 5. Course Name ASC (cuối cùng)
     */
    @Query("SELECT c FROM CourseEntity c ORDER BY c.title ASC")
    Page<CourseEntity> getAllCoursesForRevenueAnalytics(Pageable pageable);

    /**
     * Tính tổng doanh thu của course trong khoảng thời gian hiện tại (payment COMPLETED)
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0.0) " +
            "FROM PaymentEntity p " +
            "WHERE p.courseEntity.id = :courseId " +
            "AND p.status = 'COMPLETED' " +
            "AND (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
            "AND (:endDate IS NULL OR p.modifiedDate < :endDate)")
    Double getRevenueByCourseInPeriod(@Param("courseId") Long courseId,
                                     @Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate);

    /**
     * Tính tổng doanh thu của course trong khoảng thời gian trước đó (để tính growth rate)
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0.0) " +
            "FROM PaymentEntity p " +
            "WHERE p.courseEntity.id = :courseId " +
            "AND p.status = 'COMPLETED' " +
            "AND (:previousStartDate IS NULL OR p.modifiedDate >= :previousStartDate) " +
            "AND (:previousEndDate IS NULL OR p.modifiedDate < :previousEndDate)")
    Double getPreviousRevenueByCourse(@Param("courseId") Long courseId,
                                     @Param("previousStartDate") Date previousStartDate,
                                     @Param("previousEndDate") Date previousEndDate);

    /**
     * Đếm số lượng đơn hàng thành công của course trong khoảng thời gian hiện tại
     */
    @Query("SELECT COUNT(p.id) " +
            "FROM PaymentEntity p " +
            "WHERE p.courseEntity.id = :courseId " +
            "AND p.status = 'COMPLETED' " +
            "AND (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
            "AND (:endDate IS NULL OR p.modifiedDate < :endDate)")
    Integer getOrdersCountByCourse(@Param("courseId") Long courseId,
                                  @Param("startDate") Date startDate,
                                  @Param("endDate") Date endDate);

    /**
     * Đếm số học viên mới của course từ payment trong khoảng thời gian hiện tại (distinct userId)
     */
    @Query("SELECT COUNT(DISTINCT p.userEntity.id) " +
            "FROM PaymentEntity p " +
            "WHERE p.courseEntity.id = :courseId " +
            "AND p.status = 'COMPLETED' " +
            "AND (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
            "AND (:endDate IS NULL OR p.modifiedDate < :endDate)")
    Integer getNewStudentsByCourseFromPayment(@Param("courseId") Long courseId,
                                             @Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate);

    /**
     * Tính tổng doanh thu của tất cả course trong khoảng thời gian để tính revenue share
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0.0) " +
            "FROM PaymentEntity p " +
            "WHERE p.status = 'COMPLETED' " +
            "AND (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
            "AND (:endDate IS NULL OR p.modifiedDate < :endDate)")
    Double getTotalRevenueInPeriod(@Param("startDate") Date startDate,
                                  @Param("endDate") Date endDate);

    /**
     * Tính average rating tổng thể cho analytics: chỉ tính các review có star > 0 trong khoảng thời gian
     */
    @Query("SELECT COALESCE(AVG(CAST(r.star AS double)), 0.0) FROM ReviewEntity r WHERE (:startDate IS NULL OR r.createdDate >= :startDate) AND (:endDate IS NULL OR r.createdDate < :endDate) AND r.star > 0")
    Double getOverallAverageRating(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}