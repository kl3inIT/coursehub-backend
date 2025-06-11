package com.coursehub.service.impl;

import com.coursehub.converter.DiscountConverter;
import com.coursehub.dto.request.discount.DiscountRequestDTO;
import com.coursehub.dto.request.discount.DiscountSearchRequestDTO;
import com.coursehub.dto.request.discount.DiscountVerifyRequestDTO;
import com.coursehub.dto.response.discount.DiscountResponseDTO;
import com.coursehub.dto.response.discount.DiscountSearchResponseDTO;
import com.coursehub.dto.response.discount.DiscountVerifyResponseDTO;
import com.coursehub.entity.*;
import com.coursehub.exceptions.auth.DataNotFoundException;
import com.coursehub.exceptions.discount.DiscountDeletionNotAllowedException;
import com.coursehub.exceptions.discount.DiscountDuplicateException;
import com.coursehub.repository.CategoryDiscountRepository;
import com.coursehub.repository.CourseDiscountRepository;
import com.coursehub.repository.DiscountRepository;
import com.coursehub.repository.UserDiscountRepository;
import com.coursehub.service.CourseService;
import com.coursehub.service.DiscountService;
import com.coursehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final CourseDiscountRepository courseDiscountRepository;
    private final CourseService courseService;
    private final CategoryDiscountRepository categoryDiscountRepository;
    private final UserDiscountRepository userDiscountRepository;
    private final DiscountConverter discountConverter;
    private final UserService userService;
    @Override
    public DiscountVerifyResponseDTO verifyDiscountCode(DiscountVerifyRequestDTO discountVerifyRequestDTO) {
        DiscountEntity discountEntity = discountRepository.findByCodeAndIsActive(discountVerifyRequestDTO.getCode(), 1L);
        if (discountEntity == null) {
            throw new DataNotFoundException("Discount code is not valid");
        }
        CourseEntity courseEntity = courseService.findCourseEntityById(discountVerifyRequestDTO.getCourseId());
        Long categoryId = courseEntity.getCategoryEntity().getId();
        List<CourseDiscountEntity> courseDiscountEntity = courseDiscountRepository.findByCourseEntity_Id(discountVerifyRequestDTO.getCourseId());
        List<CategoryDiscountEntity> categoryDiscountEntity = categoryDiscountRepository.findByCategoryEntity_Id(categoryId);
        UserDiscountEntity userDiscountEntity = userDiscountRepository.findByUserEntity_IdAndDiscountEntity_IdAndIsActive(
                userService.getMyInfo().getId(), discountEntity.getId(), 1L
        );
        if ((discountEntity.getIsActive() == 0 && discountEntity.getIsGlobal() == 0)
                || discountEntity.getExpiryDate().before(new Date())
                || (courseDiscountEntity.isEmpty() && categoryDiscountEntity.isEmpty())
                || userDiscountEntity == null
        ) {
            throw new DataNotFoundException("Discount code is not valid");
        }
        DiscountVerifyResponseDTO discountVerifyResponseDTO = new DiscountVerifyResponseDTO();
        discountVerifyResponseDTO.setPercentage(discountEntity.getPercentage());
        discountVerifyResponseDTO.setIsValid(true);
        return discountVerifyResponseDTO;
    }

    @Override
    public DiscountResponseDTO createDiscount(DiscountRequestDTO discountRequestDTO) {

        if(discountRequestDTO.getId() == null && discountRepository.findByCode(discountRequestDTO.getCode().trim().toUpperCase()) != null) {
            throw new DiscountDuplicateException("Discount code already exists");
        }
        DiscountEntity discountEntity = discountConverter.toEntity(discountRequestDTO);
        discountRepository.save(discountEntity);
        return discountConverter.toDto(discountEntity);
    }

    @Override
    public Page<DiscountSearchResponseDTO> searchDiscount(DiscountSearchRequestDTO discountSearchRequestDTO) {
        Pageable pageable = PageRequest.of(discountSearchRequestDTO.getPage(), discountSearchRequestDTO.getSize());
        Page<DiscountEntity> discountEntities = discountRepository.searchDiscounts(
                discountSearchRequestDTO.getIsActive(),
                discountSearchRequestDTO.getCategoryId(),
                discountSearchRequestDTO.getCourseId(),
                discountSearchRequestDTO.getPercentage(),
                pageable
        );
        return discountConverter.toSearchResponseDTO(discountEntities);
    }

    @Override
    public String deleteDiscount(Long id) {
        DiscountEntity discountEntity = discountRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Discount not found"));

        if(!discountEntity.getUserDiscountEntities().isEmpty()) {
            throw new DiscountDeletionNotAllowedException("Cannot delete discount because it related data exists");
        }

        discountRepository.delete(discountEntity);
        return "Discount deleted successfully";
    }



}
