package com.coursehub.service;

import java.math.BigDecimal;

public interface PaymentService {
    BigDecimal getTotalRevenueByCourseId(Long courseId);
} 