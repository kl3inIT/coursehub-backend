package com.coursehub.service;

import com.coursehub.dto.request.payment.PaymentRequestDTO;
import com.coursehub.dto.response.payment.PaymentResponseDTO;
import com.coursehub.dto.response.payment.PaymentStatusResponseDTO;

import java.util.Map;

public interface PaymentService {
    PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO);
    void completePayment(String transactionCode);
    void sendInvoiceToEmail(Map<String, Object> invoiceDate, String transactionCode);
    PaymentStatusResponseDTO getPaymentStatus(String transactionCode);
    void setPaymentFailed(String transactionCode);
}
