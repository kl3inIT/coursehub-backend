package com.coursehub.service.impl;

import com.coursehub.dto.request.discount.DiscountRequestDTO;
import com.coursehub.dto.response.discount.DiscountResponseDTO;
import com.coursehub.entity.*;
import com.coursehub.exception.auth.DataNotFoundException;
import com.coursehub.repository.*;
import com.coursehub.service.CourseService;
import com.coursehub.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final CourseDiscountRepository courseDiscountRepository;
    private final CourseService courseService;
    private final CategoryDiscountRepository categoryDiscountRepository;
    private final UserDiscountRepository userDiscountRepository;

    @Override
    public DiscountResponseDTO verifyDiscountCode(DiscountRequestDTO discountRequestDTO) {
        DiscountEntity discountEntity = discountRepository.findByCodeAndIsActive(discountRequestDTO.getCode(), 1L);
        if (discountEntity == null) {
            throw new DataNotFoundException("Discount code is not valid");
        }
        CourseEntity courseEntity = courseService.findCourseEntityById(discountRequestDTO.getCourseId());
        Long categoryId = courseEntity.getCategoryEntity().getId();
        CourseDiscountEntity courseDiscountEntity = courseDiscountRepository.findByCourseEntity_Id(discountRequestDTO.getCourseId());
        CategoryDiscountEntity categoryDiscountEntity = categoryDiscountRepository.findByCategoryEntity_Id(categoryId);
        UserDiscountEntity userDiscountEntity = userDiscountRepository.findByDiscountEntity_IdAndIsActive(discountEntity.getId(), 1L);
        if ((discountEntity.getIsActive() == 0 && discountEntity.getIsGlobal() == 0)
                || discountEntity.getExpiryDate().before(new Date())
                || discountEntity.getQuantity() <= 0
                || (courseDiscountEntity == null && categoryDiscountEntity == null)
                || userDiscountEntity == null
        ) {
            throw new DataNotFoundException("Discount code is not valid");
        }
        DiscountResponseDTO discountResponseDTO = new DiscountResponseDTO();
        discountResponseDTO.setPercentage(discountEntity.getPercentage());
        discountResponseDTO.setIsValid(true);
        return discountResponseDTO;
    }
}
