package com.coursehub.repository;

import com.coursehub.entity.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentEntity p WHERE p.courseEntity.id = :courseId")
    BigDecimal getTotalRevenueByCourseId(@Param("courseId") Long courseId);
    PaymentEntity findByTransactionCode(String transactionCode);

    @Query("""
             SELECT p FROM PaymentEntity p
             LEFT JOIN p.userEntity u
             LEFT JOIN p.courseEntity c
             WHERE\s
                 (:startDate IS NULL OR p.modifiedDate >= :startDate) AND
                 (:endDate IS NULL OR p.modifiedDate <= :endDate) AND
                 (:status IS NULL OR p.status = :status) AND
                 (
                     :nameSearch IS NULL OR
                     LOWER(u.email) LIKE LOWER(CONCAT('%', :nameSearch, '%')) OR
                     LOWER(p.transactionCode) LIKE LOWER(CONCAT('%', :nameSearch, '%')) OR
                     LOWER(c.title) LIKE LOWER(CONCAT('%', :nameSearch, '%'))
                 )
             ORDER BY p.modifiedDate DESC
            \s""")
    List<PaymentEntity> searchPayments(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status,
            @Param("nameSearch") String nameSearch
    );

    @Query("""
             SELECT p FROM PaymentEntity p
             LEFT JOIN p.userEntity u
             LEFT JOIN p.courseEntity c
             WHERE\s
                 (:startDate IS NULL OR p.modifiedDate >= :startDate) AND
                 (:endDate IS NULL OR p.modifiedDate <= :endDate) AND
                 (:status IS NULL OR p.status = :status) AND
                 (
                     :nameSearch IS NULL OR
                     LOWER(u.email) LIKE LOWER(CONCAT('%', :nameSearch, '%')) OR
                     LOWER(p.transactionCode) LIKE LOWER(CONCAT('%', :nameSearch, '%')) OR
                     LOWER(c.title) LIKE LOWER(CONCAT('%', :nameSearch, '%'))
                 )
             ORDER BY p.modifiedDate DESC
            \s""")
    Page<PaymentEntity> searchPayments(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status,
            @Param("nameSearch") String nameSearch,
            Pageable pageable
    );

    @Query("""
             SELECT p FROM PaymentEntity p
             LEFT JOIN p.userEntity u
             LEFT JOIN p.courseEntity c
             WHERE
                 (:startDate IS NULL OR p.modifiedDate >= :startDate) AND
                 (:endDate IS NULL OR p.modifiedDate <= :endDate) AND
                 (:status IS NULL OR p.status = :status) AND
                 (:userId IS NULL OR u.id = :userId) AND
                 (
                     :nameSearch IS NULL OR
                     LOWER(u.email) LIKE LOWER(CONCAT('%', :nameSearch, '%')) OR
                     LOWER(p.transactionCode) LIKE LOWER(CONCAT('%', :nameSearch, '%')) OR
                     LOWER(c.title) LIKE LOWER(CONCAT('%', :nameSearch, '%'))
                 )
             ORDER BY p.modifiedDate DESC
            """)
    Page<PaymentEntity> searchMyPayments(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status,
            @Param("nameSearch") String nameSearch,
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentEntity p WHERE p.status = 'Completed' AND p.modifiedDate BETWEEN :startDate AND :endDate")
    BigDecimal sumCompletedPayments(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT MONTH(p.createdDate) as month, SUM(p.amount) as total FROM PaymentEntity p WHERE YEAR(p.createdDate) = :year AND p.status = 'COMPLETED' GROUP BY MONTH(p.createdDate)")
    List<Object[]> sumRevenueByMonth(@Param("year") int year);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentEntity p WHERE p.status = 'Success'")
    BigDecimal sumTotalCompletedPayments();
}
