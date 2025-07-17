package com.coursehub.service;

import com.coursehub.dto.request.payment.PaymentHistoryRequestDTO;
import com.coursehub.dto.request.payment.PaymentRequestDTO;
import com.coursehub.dto.response.payment.PaymentHistoryResponseDTO;
import com.coursehub.dto.response.payment.PaymentResponseDTO;
import com.coursehub.dto.response.payment.PaymentStatusResponseDTO;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO);
    void completePayment(String transactionCode);
    void sendInvoiceToEmail(Map<String, Object> invoiceDate, String transactionCode);
    PaymentStatusResponseDTO getPaymentStatus(String transactionCode);
    void setPaymentFailed(String transactionCode);
    Page<PaymentHistoryResponseDTO> getPaymentHistory(PaymentHistoryRequestDTO paymentHistoryRequestDTO);
    ByteArrayInputStream exportToExcel(PaymentHistoryRequestDTO paymentHistoryRequestDTO);
    Map<String, String> getPaymentOverall(PaymentHistoryRequestDTO paymentHistoryRequestDTO);
    Page<PaymentHistoryResponseDTO> getMyPaymentHistory(PaymentHistoryRequestDTO paymentHistoryRequestDTO);
    BigDecimal getTotalRevenueByCourseId(Long courseId);
    List<PaymentHistoryResponseDTO> getAllPaymentHistory(PaymentHistoryResponseDTO paymentHistoryResponseDTO);
    BigDecimal getTotalRevenue();
    Long countTotalPayments();
}
