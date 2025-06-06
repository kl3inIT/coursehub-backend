package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.payment.PaymentRequestDTO;
import com.coursehub.dto.response.payment.PaymentResponseDTO;
import com.coursehub.dto.response.payment.PaymentStatusResponseDTO;
import com.coursehub.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {


    @Value("${SEPAY_WEBHOOK_APIKEY}")
    private String sepaySecretKey;

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
        paymentService.sendInvoiceToEmail(payload, transactionCode);
        return null;
    }



    @PostMapping("/init")
    public ResponseEntity<ResponseGeneral<PaymentResponseDTO>> createPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        ResponseGeneral<PaymentResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(paymentService.createPayment(paymentRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{transactionCode}/payment-status")
    public ResponseEntity<ResponseGeneral<PaymentStatusResponseDTO>> getPaymentStatus(@PathVariable String transactionCode) {
        ResponseGeneral<PaymentStatusResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(paymentService.getPaymentStatus(transactionCode));
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{transactionCode}/expired")
    public ResponseEntity<ResponseGeneral<String>> failedOrder(@PathVariable String transactionCode) {
        ResponseGeneral<String> responseDTO = new ResponseGeneral<>();
        paymentService.setPaymentFailed(transactionCode);
        responseDTO.setMessage("Success");
        responseDTO.setData("Payment status updated to failed for transaction: " + transactionCode);
        return ResponseEntity.ok(responseDTO);
    }


}
