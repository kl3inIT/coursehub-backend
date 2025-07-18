package com.coursehub.service.impl;

import com.coursehub.converter.DiscountConverter;
import com.coursehub.dto.request.discount.DiscountRequestDTO;
import com.coursehub.dto.request.discount.DiscountSearchRequestDTO;
import com.coursehub.dto.response.discount.DiscountResponseDTO;
import com.coursehub.dto.response.discount.DiscountSearchResponseDTO;
import com.coursehub.entity.DiscountEntity;
import com.coursehub.entity.UserDiscountEntity;
import com.coursehub.enums.DiscountStatus;
import com.coursehub.exceptions.auth.DataNotFoundException;
import com.coursehub.exceptions.discount.DiscountDeletionNotAllowedException;
import com.coursehub.repository.DiscountRepository;
import com.coursehub.repository.UserDiscountRepository;
import com.coursehub.service.DiscountService;
import com.coursehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final UserDiscountRepository userDiscountRepository;
    private final DiscountConverter discountConverter;
    private final UserService userService;


    @Override
    public List<DiscountResponseDTO> getMyDiscountByCourseId(Long courseId) {
        Long userId = userService.getMyInfo().getId();
        LocalDateTime now = LocalDateTime.now();
        List<DiscountEntity> discountEntities = userDiscountRepository.findActiveDiscountsByCourseId(userId, 1L,1L, courseId, now)
                .stream()
                .map(UserDiscountEntity::getDiscountEntity)
                .toList();
        return discountConverter.toDtoList(discountEntities);
    }

    @Override
    public Page<DiscountSearchResponseDTO> searchMyAvailableDiscount(DiscountSearchRequestDTO discountSearchRequestDTO) {
        Long userId = userService.getMyInfo().getId();
        Pageable pageable = PageRequest.of(discountSearchRequestDTO.getPage(), discountSearchRequestDTO.getSize());
        LocalDateTime now = LocalDateTime.now();
        Page<DiscountEntity> discountEntities = discountRepository.searchDiscountsOwner(
                discountSearchRequestDTO.getCategoryId(),
                discountSearchRequestDTO.getCourseId(),
                userId,
                discountSearchRequestDTO.getPercentage(),
                now,
                pageable
        );
        return discountConverter.toSearchResponseDTO(discountEntities);
    }




    @Override
    public DiscountResponseDTO createDiscount(DiscountRequestDTO discountRequestDTO) {
        DiscountEntity discountEntity = discountConverter.toEntity(discountRequestDTO);
        discountRepository.save(discountEntity);
        return discountConverter.toDto(discountEntity);
    }


    @Override
    public Page<DiscountSearchResponseDTO> searchAvailableDiscount(DiscountSearchRequestDTO discountSearchRequestDTO) {
        Pageable pageable = PageRequest.of(discountSearchRequestDTO.getPage(), discountSearchRequestDTO.getSize());
        Page<DiscountEntity> discountEntities = discountRepository.searchAvailableDiscounts(
                discountSearchRequestDTO.getIsActive(),
                discountSearchRequestDTO.getCategoryId(),
                discountSearchRequestDTO.getCourseId(),
                discountSearchRequestDTO.getPercentage(),
                DiscountStatus.AVAILABLE.status(),
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

    @Override
    public Page<DiscountSearchResponseDTO> searchDiscount(DiscountSearchRequestDTO discountSearchRequestDTO) {
        Pageable pageable = PageRequest.of(discountSearchRequestDTO.getPage(), discountSearchRequestDTO.getSize());
        Page<DiscountEntity> discountEntities = discountRepository.searchDiscounts(
                discountSearchRequestDTO.getCategoryId(),
                discountSearchRequestDTO.getCourseId(),
                discountSearchRequestDTO.getPercentage(),
                DiscountStatus.getDiscountStatus().get(discountSearchRequestDTO.getStatus()),
                pageable
        );
        return discountConverter.toSearchResponseDTO(discountEntities);
    }

    @Override
    public Map<String, String> getDiscountStatus() {
        return DiscountStatus.getDiscountStatus();
    }

    @Override
    public Map<String, String> getOverall() {
        Map<String, String> result = new HashMap<>();
        result.put("totalDiscounts", String.valueOf(discountRepository.count()));
        result.put("activeDiscounts", String.valueOf(discountRepository.countByIsActive(1L)));
        result.put("totalUsage", String.valueOf(userDiscountRepository.countByIsActive(0L)));
        return result;
    }


}
