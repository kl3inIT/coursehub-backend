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
import com.coursehub.exceptions.pdf.PdfException;
import com.coursehub.repository.EnrollmentRepository;
import com.coursehub.repository.PaymentRepository;
import com.coursehub.repository.UserDiscountRepository;
import com.coursehub.service.PaymentService;
import com.coursehub.service.UserService;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<PaymentHistoryResponseDTO> getAllPaymentHistory(PaymentHistoryResponseDTO paymentHistoryResponseDTO) {
        List<PaymentEntity> entities = paymentRepository.findAll();
        List<PaymentHistoryResponseDTO> dtos = paymentConverter.toPaymentHistoryResponseDTO(entities);
        return dtos;
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return paymentRepository.sumTotalCompletedPayments();
    }

    @Override
    public Long countTotalPayments() {
        return paymentRepository.count();
    }

    @Override
    public BigDecimal getTotalRevenueByCourseId(Long courseId) {
        return paymentRepository.getTotalRevenueByCourseId(courseId);
    }

    @Override
    public byte[] generateOrderPdf(String transactionCode) {
        PaymentEntity payment = paymentRepository.findByTransactionCode(transactionCode.toUpperCase().trim());
        if(payment == null){
            throw new DataNotFoundException("Payment not found for transaction code: " + transactionCode);
        }

        // Read template from resources
        try{
            ClassPathResource templateResource = new ClassPathResource("templates/invoice_template_coursehub.pdf");
            PdfReader reader = new PdfReader(templateResource.getInputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(reader, writer);

            // Get form from PDF
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            // Fill data into fields
            form.getField("field_courseName").setValue(payment.getCourseEntity().getTitle());
            form.getField("field_transactionCode").setValue(payment.getTransactionCode());
            form.getField("field_time").setValue(payment.getModifiedDate().toString());
            form.getField("field_paymentMethod").setValue(payment.getMethod() == null ? "Bank Transfer" : payment.getMethod());
            form.getField("field_totalAmount").setValue(String.format("%.2f USD", payment.getAmount()));

            // Flatten form to lock fields
            form.flattenFields();

            pdf.close();
            return baos.toByteArray();
        } catch(Exception e) {
            throw new PdfException("Error generating PDF: " + e.getMessage());
        }
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
            UserDiscountEntity userDiscountEntity = userDiscountRepository.findByDiscountEntity_IdAndIsActiveAndUserEntity_Id(
                    discountEntity.getId(), 1L, paymentEntity.getUserEntity().getId());
            userDiscountEntity.setIsActive(0L);
            userDiscountRepository.save(userDiscountEntity);
            discountScheduler.updateDiscountStatus(discountEntity);
        }
    }

    @Override
    public void sendInvoiceToEmail(Map<String, Object> invoiceData, String transactionCode, String downloadLink) {
        Map<String, String> result = new HashMap<>();
        PaymentEntity paymentEntity = paymentRepository.findByTransactionCode(transactionCode);
        CourseEntity courseEntity = paymentEntity.getCourseEntity();
        result.put("courseName", courseEntity.getTitle());
        result.put("orderId", transactionCode);
        result.put("purchaseDate", invoiceData.get("transactionDate").toString());
        result.put("totalAmount", invoiceData.get("transferAmount").toString());
        result.put("email", paymentEntity.getUserEntity().getEmail());
        result.put("paymentMethod", invoiceData.get("gateway").toString());
        otpUtil.sendInvoiceToEmail(result, downloadLink);
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

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Payment Report");

            // ========== Styles ==========
            // Title Style
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.LEFT);

            // Info Style
            CellStyle infoStyle = workbook.createCellStyle();
            Font infoFont = workbook.createFont();
            infoFont.setItalic(true);
            infoStyle.setFont(infoFont);

            // Total Amount Style
            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalFont.setColor(IndexedColors.RED.getIndex());
            totalStyle.setFont(totalFont);

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Data Style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Conditional Styles for Status
            CellStyle completedStyle = workbook.createCellStyle();
            completedStyle.cloneStyleFrom(dataStyle);
            completedStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            completedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle failedStyle = workbook.createCellStyle();
            failedStyle.cloneStyleFrom(dataStyle);
            failedStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            failedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle pendingStyle = workbook.createCellStyle();
            pendingStyle.cloneStyleFrom(dataStyle);
            pendingStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            pendingStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // ========== Header Section (Merge cells like Word) ==========
            // Row 1: Title
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Payment Report for CourseHub");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            // Row 2: Date
            Row dateRow = sheet.createRow(1);
            Cell todayLabel = dateRow.createCell(0);
            todayLabel.setCellValue("Today:");
            todayLabel.setCellStyle(infoStyle);
            Cell todayValue = dateRow.createCell(1);
            todayValue.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            todayValue.setCellStyle(infoStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 5));

            // Row 3: Report Period
            Row periodRow = sheet.createRow(2);
            Cell periodLabel = periodRow.createCell(0);
            periodLabel.setCellValue("Report Period:");
            periodLabel.setCellStyle(infoStyle);
            Cell periodValue = periodRow.createCell(1);
            String startDate = paymentHistoryRequestDTO.getStartDate() != null ?
                    paymentHistoryRequestDTO.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
            String endDate = paymentHistoryRequestDTO.getEndDate() != null ?
                    paymentHistoryRequestDTO.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
            periodValue.setCellValue(startDate + " - " + endDate);
            periodValue.setCellStyle(infoStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 5));

            // Row 4: Total Amount Due
            Row totalRow = sheet.createRow(4);
            Cell totalLabel = totalRow.createCell(0);
            totalLabel.setCellValue("Total revenue from completed payments:");
            totalLabel.setCellStyle(totalStyle);
            sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 3));

            Map<String, String> paymentOverall = getPaymentOverall(paymentHistoryRequestDTO);
            Cell totalValue = totalRow.createCell(4);
            totalValue.setCellValue(String.format("%.2f", Double.parseDouble(paymentOverall.get("totalAmount"))));
            totalValue.setCellStyle(totalStyle);
            sheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 5));

            // ========== Table Header ==========
            Row headerRow = sheet.createRow(6);
            String[] headers = {"TransactionCode", "CourseName", "Username", "Amount", "Status", "Date"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ========== Table Data ==========
            int rowNum = 7;
            for (PaymentHistoryResponseDTO payment : paymentHistoryList) {
                Row row = sheet.createRow(rowNum++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(payment.getTransactionCode());
                cell0.setCellStyle(dataStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(payment.getCourseName());
                cell1.setCellStyle(dataStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(payment.getUserName());
                cell2.setCellStyle(dataStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(String.format("%.2f", payment.getAmount()));
                cell3.setCellStyle(dataStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(payment.getStatus());
                // Apply conditional color
                if ("Completed".equalsIgnoreCase(payment.getStatus())) {
                    cell4.setCellStyle(completedStyle);
                } else if ("Failed".equalsIgnoreCase(payment.getStatus())) {
                    cell4.setCellStyle(failedStyle);
                } else if ("Pending".equalsIgnoreCase(payment.getStatus())) {
                    cell4.setCellStyle(pendingStyle);
                } else {
                    cell4.setCellStyle(dataStyle);
                }

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(payment.getDate().toString());
                cell5.setCellStyle(dataStyle);
            }

            // ========== Auto-size Columns ==========
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new ExcelException("Error while exporting payment history to Excel: " + e.getMessage());
        }
    }

    @Override
    public Map<String, String> getPaymentOverall(PaymentHistoryRequestDTO paymentHistoryRequestDTO) {
        List<PaymentEntity> paymentEntities = paymentRepository.searchPayments(
                paymentHistoryRequestDTO.getStartDate(),
                paymentHistoryRequestDTO.getEndDate(),
                paymentHistoryRequestDTO.getStatus(),
                paymentHistoryRequestDTO.getNameSearch()
        );
        List<PaymentHistoryResponseDTO> paymentHistoryList = paymentConverter.toPaymentHistoryResponseDTO(paymentEntities);
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
