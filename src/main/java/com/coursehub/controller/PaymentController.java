package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.payment.PaymentHistoryRequestDTO;
import com.coursehub.dto.request.payment.PaymentRequestDTO;
import com.coursehub.dto.response.payment.PaymentHistoryResponseDTO;
import com.coursehub.dto.response.payment.PaymentResponseDTO;
import com.coursehub.dto.response.payment.PaymentStatusResponseDTO;
import com.coursehub.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.coursehub.constant.Constant.CommonConstants.*;
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {


    @Value("${SEPAY_WEBHOOK_APIKEY}")
    private String sepaySecretKey;

    @Value("${domain}")
    private String domain;

    private final PaymentService paymentService;

    @PostMapping("/sepay/hook")
    public Void verifyOrder(@RequestBody Map<String, Object> payload,
                            @RequestHeader("Authorization") String apiKey) {
        // Validate API Key
        if (!apiKey.equals("Apikey " + sepaySecretKey)) {
            throw new SecurityException("Invalid API Key");
        }

        String regex = "[A-Za-z0-9]{12}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher((String) payload.get("content"));
        String transactionCode = null;
        if (matcher.find()) {
            transactionCode = matcher.group();
        } else {
            throw new IllegalArgumentException("Not Found transaction code");
        }
        paymentService.completePayment(transactionCode);

        String downloadLink = domain + "/api/payments/download/invoice/" + transactionCode;
        paymentService.sendInvoiceToEmail(payload, transactionCode, downloadLink);
        return null;
    }



    @GetMapping("/download/invoice/{transactionCode}")
    public ResponseEntity<ByteArrayResource> downloadOrderPdf(@PathVariable String transactionCode) {

        byte[] pdfBytes = paymentService.generateOrderPdf(transactionCode);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=order_" + transactionCode + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);
    }

    @PostMapping("/init")
    public ResponseEntity<ResponseGeneral<PaymentResponseDTO>> createPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        ResponseGeneral<PaymentResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(paymentService.createPayment(paymentRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{transactionCode}/payment-status")
    public ResponseEntity<ResponseGeneral<PaymentStatusResponseDTO>> getPaymentStatus(@PathVariable String transactionCode) {
        ResponseGeneral<PaymentStatusResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(paymentService.getPaymentStatus(transactionCode));
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{transactionCode}/expired")
    public ResponseEntity<ResponseGeneral<String>> failedOrder(@PathVariable String transactionCode) {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        paymentService.setPaymentFailed(transactionCode);
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData("Payment status updated to failed for transaction: " + transactionCode);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping()
    public ResponseEntity<ResponseGeneral<Page<PaymentHistoryResponseDTO>>> getPaymentHistory(@ModelAttribute PaymentHistoryRequestDTO paymentHistoryRequestDTO) {
        ResponseGeneral<Page<PaymentHistoryResponseDTO>> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(paymentService.getPaymentHistory(paymentHistoryRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseGeneral<Page<PaymentHistoryResponseDTO>>> getMyPaymentHistory(@ModelAttribute PaymentHistoryRequestDTO paymentHistoryRequestDTO) {
        ResponseGeneral<Page<PaymentHistoryResponseDTO>> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(paymentService.getMyPaymentHistory(paymentHistoryRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/overall")
    public ResponseEntity<ResponseGeneral<Map<String, String>>> getOverall(@ModelAttribute PaymentHistoryRequestDTO paymentHistoryRequestDTO) {
        ResponseGeneral<Map<String, String>> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage(SUCCESS);
        responseDTO.setData(paymentService.getPaymentOverall(paymentHistoryRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/excel")
    public ResponseEntity<InputStreamResource> exportPaymentHistoryToExcel(@ModelAttribute PaymentHistoryRequestDTO paymentHistoryRequestDTO) {
        ByteArrayInputStream excelFile = paymentService.exportToExcel(paymentHistoryRequestDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=payment_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelFile));
    }





}
