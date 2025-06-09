package com.coursehub.service;

import com.coursehub.dto.request.discount.DiscountRequestDTO;
import com.coursehub.dto.response.discount.DiscountResponseDTO;

public interface DiscountService {

    DiscountResponseDTO verifyDiscountCode(DiscountRequestDTO discountRequestDTO);

}
