package com.coursehub.repository;

import com.coursehub.entity.PaymentEntity;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentEntity p WHERE p.courseEntity.id = :courseId")
    BigDecimal getTotalRevenueByCourseId(@Param("courseId") Long courseId);
    PaymentEntity findByTransactionCode(String transactionCode);
}