package com.coursehub.service;

import com.coursehub.dto.request.discount.DiscountRequestDTO;
import com.coursehub.dto.request.discount.DiscountSearchRequestDTO;
import com.coursehub.dto.response.discount.DiscountResponseDTO;
import com.coursehub.dto.response.discount.DiscountSearchResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DiscountService {

    DiscountResponseDTO createDiscount(DiscountRequestDTO discountRequestDTO);
    List<DiscountResponseDTO> getMyDiscountByCourseId(Long courseId);
    Page<DiscountSearchResponseDTO> searchMyAvailableDiscount(DiscountSearchRequestDTO discountSearchRequestDTO);
    Page<DiscountSearchResponseDTO> searchAvailableDiscount(DiscountSearchRequestDTO discountSearchRequestDTO);
    String deleteDiscount(Long id);
    Page<DiscountSearchResponseDTO> searchDiscount(DiscountSearchRequestDTO discountSearchRequestDTO);
    Map<String, String> getDiscountStatus();
    Map<String, String> getOverall();
}
