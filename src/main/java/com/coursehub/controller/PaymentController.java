package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.discount.DiscountRequestDTO;
import com.coursehub.dto.request.payment.PaymentRequestDTO;
import com.coursehub.dto.response.discount.DiscountResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {


    @Value("${SEPAY_WEBHOOK_APIKEY}")
    private String sepaySecretKey;



    @PostMapping("/sepay/hook")
    public Void verifyOrder(@RequestBody Map<String, Object> payload,
                            @RequestHeader("Authorization") String apiKey) {
        // Validate API Key
        if (!apiKey.equals("APIkey " + sepaySecretKey)) {
            throw new SecurityException("Invalid API Key");
        }

        // Process the payload
        // Add your business logic here

        return null;
    }


    @PostMapping("/init")
    public ResponseEntity<ResponseGeneral<String>> createPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        ResponseGeneral<DiscountResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(discountService.verifyDiscountCode(discountRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }



}
