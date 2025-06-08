package com.coursehub.service.impl;

import com.coursehub.repository.PaymentRepository;
import com.coursehub.service.PaymentService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    public BigDecimal getTotalRevenueByCourseId(Long courseId) {
        return paymentRepository.getTotalRevenueByCourseId(courseId);
    }
} 