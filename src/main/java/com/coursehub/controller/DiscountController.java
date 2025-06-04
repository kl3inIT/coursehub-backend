package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.auth.AuthenticationRequestDTO;
import com.coursehub.dto.request.discount.DiscountRequestDTO;
import com.coursehub.dto.response.auth.AuthenticationResponseDTO;
import com.coursehub.dto.response.discount.DiscountResponseDTO;
import com.coursehub.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping("/verify")
    public ResponseEntity<ResponseGeneral<DiscountResponseDTO>> login(@RequestBody DiscountRequestDTO discountRequestDTO) {
        ResponseGeneral<DiscountResponseDTO> responseDTO = new ResponseGeneral<>();
        responseDTO.setMessage("Success");
        responseDTO.setData(discountService.verifyDiscountCode(discountRequestDTO));
        return ResponseEntity.ok(responseDTO);
    }

}
