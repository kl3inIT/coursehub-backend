package com.coursehub.service.impl;

import com.coursehub.components.DiscountScheduler;
import com.coursehub.components.OtpUtil;
import com.coursehub.converter.PaymentConverter;
import com.coursehub.dto.request.payment.PaymentHistoryRequestDTO;
import com.coursehub.dto.request.payment.PaymentRequestDTO;
import com.coursehub.dto.response.payment.PaymentHistoryResponseDTO;
import com.coursehub.dto.response.payment.PaymentResponseDTO;
import com.coursehub.dto.response.payment.PaymentStatusResponseDTO;
import com.coursehub.entity.*;
import com.coursehub.enums.PaymentStatus;
import com.coursehub.exceptions.auth.DataNotFoundException;
import com.coursehub.exceptions.excel.ExcelException;
import com.coursehub.repository.EnrollmentRepository;
import com.coursehub.repository.PaymentRepository;
import com.coursehub.repository.UserDiscountRepository;
import com.coursehub.service.PaymentService;
import com.coursehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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
    private final DiscountScheduler discountScheduler;
    private final UserService userService;

    @Override
    public BigDecimal getTotalRevenueByCourseId(Long courseId) {
        return paymentRepository.getTotalRevenueByCourseId(courseId);
    }

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
        enrollmentEntity.setIsCompleted(0L);
        enrollmentRepository.save(enrollmentEntity);


        // invalidate user discount if exists
        DiscountEntity discountEntity = paymentEntity.getDiscountEntity();

        if (discountEntity != null) {
            UserDiscountEntity userDiscountEntity = userDiscountRepository.findByDiscountEntity_IdAndIsActive(
                    discountEntity.getId(), 1L);
            userDiscountEntity.setIsActive(0L);
            userDiscountRepository.save(userDiscountEntity);
            discountScheduler.updateDiscountStatus(discountEntity);
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

    @Override
    public Page<PaymentHistoryResponseDTO> getPaymentHistory(PaymentHistoryRequestDTO paymentRequestDTO) {
        Pageable pageable = PageRequest.of(paymentRequestDTO.getPage(), paymentRequestDTO.getSize());
        Page<PaymentEntity> paymentHistoryPage = paymentRepository.searchPayments(
                paymentRequestDTO.getStartDate(),
                paymentRequestDTO.getEndDate(),
                paymentRequestDTO.getStatus(),
                paymentRequestDTO.getNameSearch(),
                pageable
        );
        return paymentConverter.toPaymentHistoryResponseDTO(paymentHistoryPage);
    }

    @Override
    public ByteArrayInputStream exportToExcel(PaymentHistoryRequestDTO paymentHistoryRequestDTO) {
        List<PaymentEntity> paymentEntities = paymentRepository.searchPayments(
                paymentHistoryRequestDTO.getStartDate(),
                paymentHistoryRequestDTO.getEndDate(),
                paymentHistoryRequestDTO.getStatus(),
                paymentHistoryRequestDTO.getNameSearch()
        );
        List<PaymentHistoryResponseDTO> paymentHistoryList = paymentConverter.toPaymentHistoryResponseDTO(paymentEntities);

        if (paymentHistoryList.isEmpty()) {
            throw new DataNotFoundException("No payment history found for the given criteria");
        }
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()){
            Sheet sheet = workbook.createSheet("Payments");

            // tao header
            Row headerRow = sheet.createRow(0);
            // java reflection
            Field[] fields = PaymentHistoryResponseDTO.class.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fields[i].getName());
            }

            // tao data
            int rowNum = 1;
            for( PaymentHistoryResponseDTO payment : paymentHistoryList) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    Object value = fields[i].get(payment);
                    row.createCell(i).setCellValue(value != null ? value.toString() : "");
                }
            }

            // auto-size
            for (int i = 0; i < fields.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e){
            throw new ExcelException("Error while exporting payment history to Excel");
        }
    }

    @Override
    public Map<String, String> getPaymentOverall(PaymentHistoryRequestDTO paymentHistoryRequestDTO) {
        List<PaymentHistoryResponseDTO> paymentHistoryList = getPaymentHistory(paymentHistoryRequestDTO).getContent();
        double totalAmount = 0;
        int successfulPayments = 0;
        int pendingPayments = 0;
        int failedPayments = 0;
        for (PaymentHistoryResponseDTO payment : paymentHistoryList) {
            if(payment.getStatus().equals(PaymentStatus.COMPLETED.getStatus())) {
                totalAmount += payment.getAmount();
                ++successfulPayments;
            } else if(payment.getStatus().equals(PaymentStatus.FAILED.getStatus())) {
                ++failedPayments;
            } else{
                ++pendingPayments;
            }

        }
        Map<String, String> paymentOverall = new HashMap<>();
        paymentOverall.put("totalAmount", String.valueOf(totalAmount));
        paymentOverall.put("successfulPayments", String.valueOf(successfulPayments));
        paymentOverall.put("pendingPayments", String.valueOf(pendingPayments));
        paymentOverall.put("failedPayments", String.valueOf(failedPayments));
        return paymentOverall;
    }

    @Override
    public Page<PaymentHistoryResponseDTO> getMyPaymentHistory(PaymentHistoryRequestDTO paymentHistoryRequestDTO) {
        Pageable pageable = PageRequest.of(paymentHistoryRequestDTO.getPage(), paymentHistoryRequestDTO.getSize());
        Page<PaymentEntity> paymentHistoryPage = paymentRepository.searchMyPayments(
                paymentHistoryRequestDTO.getStartDate(),
                paymentHistoryRequestDTO.getEndDate(),
                paymentHistoryRequestDTO.getStatus(),
                paymentHistoryRequestDTO.getNameSearch(),
                userService.getMyInfo().getId(),
                pageable
        );
        return paymentConverter.toPaymentHistoryResponseDTO(paymentHistoryPage);
    }

}
