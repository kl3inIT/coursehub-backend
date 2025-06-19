package com.coursehub.repository;

import com.coursehub.entity.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
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
}
