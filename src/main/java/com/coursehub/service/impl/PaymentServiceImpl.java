package com.coursehub.service.impl;

import com.coursehub.components.OtpUtil;
import com.coursehub.converter.PaymentConverter;
import com.coursehub.dto.request.payment.PaymentRequestDTO;
import com.coursehub.dto.response.payment.PaymentResponseDTO;
import com.coursehub.dto.response.payment.PaymentStatusResponseDTO;
import com.coursehub.entity.*;
import com.coursehub.enums.PaymentStatus;
import com.coursehub.exceptions.auth.DataNotFoundException;
import com.coursehub.repository.EnrollmentRepository;
import com.coursehub.repository.PaymentRepository;
import com.coursehub.repository.UserDiscountRepository;
import com.coursehub.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {


    private final PaymentRepository paymentRepository;
    private final PaymentConverter paymentConverter;
    private final UserDiscountRepository userDiscountRepository;
    private final OtpUtil otpUtil;
    private final EnrollmentRepository enrollmentRepository;
    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO) {
        PaymentEntity paymentEntity = paymentConverter.toPaymentEntity(paymentRequestDTO);
        paymentRepository.save(paymentEntity);
        return paymentConverter.toPaymentResponseDTO(paymentEntity);
    }

    @Override
    public void completePayment(String transactionCode) {
        PaymentEntity paymentEntity = paymentRepository.findByTransactionCode(transactionCode);
        paymentEntity.setStatus(PaymentStatus.COMPLETED.getStatus());
        paymentRepository.save(paymentEntity);

        // enroll user in the course
        EnrollmentEntity enrollmentEntity = new EnrollmentEntity();
        enrollmentEntity.setUserEntity(paymentEntity.getUserEntity());
        enrollmentEntity.setCourseEntity(paymentEntity.getCourseEntity());
        enrollmentRepository.save(enrollmentEntity);


        // invalidate user discount if exists
        DiscountEntity discountEntity = paymentEntity.getDiscountEntity();

        if (discountEntity != null) {
            UserDiscountEntity userDiscountEntity = userDiscountRepository.findByDiscountEntity_IdAndIsActive(
                    discountEntity.getId(), 1L);
            userDiscountEntity.setIsActive(0L);
            userDiscountRepository.save(userDiscountEntity);
        }

    }



    @Override
    public void sendInvoiceToEmail(Map<String, Object> invoiceData, String transactionCode) {
        Map<String, String> result = new HashMap<>();
        PaymentEntity paymentEntity = paymentRepository.findByTransactionCode(transactionCode);
        CourseEntity courseEntity = paymentEntity.getCourseEntity();
        result.put("courseName", courseEntity.getTitle());
        result.put("orderId", transactionCode);
        result.put("purchaseDate", invoiceData.get("transactionDate").toString());
        result.put("totalAmount", invoiceData.get("transferAmount").toString());
        result.put("email", paymentEntity.getUserEntity().getEmail());
        result.put("paymentMethod", invoiceData.get("gateway").toString());
        otpUtil.sendInvoiceToEmail(result);
    }

    @Override
    public PaymentStatusResponseDTO getPaymentStatus(String transactionCode) {
        PaymentEntity paymentEntity = paymentRepository.findByTransactionCode(transactionCode);
        if(paymentEntity == null) {
            throw new DataNotFoundException("Payment not found for transaction code: " + transactionCode);
        }

        if(paymentEntity.getStatus().equals(PaymentStatus.COMPLETED.getStatus())) {
            return PaymentStatusResponseDTO.builder()
                    .isPaid(true)
                    .build();
        } else {
            return PaymentStatusResponseDTO.builder()
                    .isPaid(false)
                    .build();
        }
    }

    @Override
    public void setPaymentFailed(String transactionCode) {
        PaymentEntity paymentEntity = paymentRepository.findByTransactionCode(transactionCode);
        if (paymentEntity == null) {
            throw new DataNotFoundException("Payment not found for transaction code: " + transactionCode);
        }
        paymentEntity.setStatus(PaymentStatus.FAILED.getStatus());
    }

}
