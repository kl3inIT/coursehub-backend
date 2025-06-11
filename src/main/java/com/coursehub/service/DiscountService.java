package com.coursehub.service;

import com.coursehub.dto.request.discount.DiscountRequestDTO;
import com.coursehub.dto.request.discount.DiscountSearchRequestDTO;
import com.coursehub.dto.request.discount.DiscountVerifyRequestDTO;
import com.coursehub.dto.response.discount.DiscountResponseDTO;
import com.coursehub.dto.response.discount.DiscountSearchResponseDTO;
import com.coursehub.dto.response.discount.DiscountVerifyResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DiscountService {

    DiscountVerifyResponseDTO verifyDiscountCode(DiscountVerifyRequestDTO discountVerifyRequestDTO);
    DiscountResponseDTO createDiscount(DiscountRequestDTO discountRequestDTO);
    Page<DiscountSearchResponseDTO> searchDiscount(DiscountSearchRequestDTO discountSearchRequestDTO);
    String deleteDiscount(Long id);


}
