package com.coursehub.repository;
import com.coursehub.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface AnalyticsRepository extends JpaRepository<CategoryEntity, Long> {

    // CATEGORY ANALYTICS QUERIES
    @Query(
        "SELECT COUNT(DISTINCT co.id) " +
        "FROM CourseEntity co " +
        "WHERE co.categoryEntity.id = :categoryId " +
        "AND co.createdDate BETWEEN :startDate AND :endDate"
    )
    Long countCoursesByCategoryAndPeriod(
        @Param("categoryId") Long categoryId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );

    @Query(
        "SELECT COUNT(DISTINCT e.userEntity.id) " +
        "FROM EnrollmentEntity e " +
        "WHERE e.courseEntity.categoryEntity.id = :categoryId " +
        "AND e.createdDate BETWEEN :startDate AND :endDate"
    )
    Long countStudentsByCategoryAndPeriod(
        @Param("categoryId") Long categoryId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );

    @Query(
        "SELECT COALESCE(SUM(p.amount), 0.0) " +
        "FROM PaymentEntity p " +
        "WHERE p.courseEntity.categoryEntity.id = :categoryId " +
        "AND p.status = 'COMPLETED' " +
        "AND (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
        "AND (:endDate IS NULL OR p.modifiedDate < :endDate)"
    )
    Double sumRevenueByCategoryAndPeriod(
        @Param("categoryId") Long categoryId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );

    // COURSE ANALYTICS QUERIES
    @Query(
        "SELECT COUNT(DISTINCT e.userEntity.id) " +
        "FROM EnrollmentEntity e " +
        "WHERE e.courseEntity.id = :courseId " +
        "AND e.createdDate BETWEEN :startDate AND :endDate"
    )
    Integer countStudentsByCourseAndPeriod(
        @Param("courseId") Long courseId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );

    @Query(
        "SELECT COALESCE(AVG(CAST(r.star AS double)), 0.0) " +
        "FROM ReviewEntity r " +
        "WHERE r.courseEntity.id = :courseId " +
        "AND r.createdDate BETWEEN :startDate AND :endDate"
    )
    Double avgRatingByCourseAndPeriod(
        @Param("courseId") Long courseId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );

    @Query(
        "SELECT COALESCE(SUM(p.amount), 0.0) " +
        "FROM PaymentEntity p " +
        "WHERE p.courseEntity.id = :courseId " +
        "AND p.status = 'COMPLETED' " +
        "AND (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
        "AND (:endDate IS NULL OR p.modifiedDate < :endDate)"
    )
    Double sumRevenueByCourseAndPeriod(
        @Param("courseId") Long courseId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );

    @Query(
        "SELECT COUNT(r.id) " +
        "FROM ReviewEntity r " +
        "WHERE r.courseEntity.id = :courseId " +
        "AND r.createdDate BETWEEN :startDate AND :endDate"
    )
    Long countReviewsByCourseAndPeriod(
        @Param("courseId") Long courseId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );

    @Query(
        "SELECT COALESCE(SUM(p.amount), 0.0) " +
        "FROM PaymentEntity p " +
        "WHERE p.status = 'COMPLETED' " +
        "AND (:startDate IS NULL OR p.modifiedDate >= :startDate) " +
        "AND (:endDate IS NULL OR p.modifiedDate < :endDate)"
    )
    Double getTotalCourseRevenue(
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );
}