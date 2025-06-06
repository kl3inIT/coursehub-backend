package com.coursehub.repository;

import com.coursehub.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    PaymentEntity findByTransactionCode(String transactionCode);
}
