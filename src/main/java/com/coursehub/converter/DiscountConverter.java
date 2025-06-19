package com.coursehub.converter;

import com.coursehub.components.DiscountScheduler;
import com.coursehub.dto.request.discount.DiscountRequestDTO;
import com.coursehub.dto.response.discount.DiscountResponseDTO;
import com.coursehub.dto.response.discount.DiscountSearchResponseDTO;
import com.coursehub.entity.*;
import com.coursehub.enums.DiscountStatus;
import com.coursehub.exceptions.auth.DataNotFoundException;
import com.coursehub.exceptions.discount.QuantityException;
import com.coursehub.repository.DiscountRepository;
import com.coursehub.service.CategoryService;
import com.coursehub.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiscountConverter {

    private final ModelMapper modelMapper;
    private final CourseService courseService;
    private final CategoryService categoryService;
    private final DiscountRepository discountRepository;
    private final DiscountScheduler discountScheduler;

    public DiscountEntity toEntity(DiscountRequestDTO discountRequestDTO) {
        DiscountEntity discountEntity;

        if (discountRequestDTO.getId() != null) {
            //cast update
            discountEntity = discountRepository.findById(discountRequestDTO.getId())
                    .orElseThrow(() -> new DataNotFoundException("Discount not found"));
            if(discountRequestDTO.getQuantity() < discountEntity.getUserDiscountEntities().size()){
                throw new QuantityException("This quantity is less than the quantity that the user has taken.");
            }
            modelMapper.map(discountRequestDTO, discountEntity);

            discountEntity.getCourseDiscountEntities().clear();
            discountEntity.getCategoryDiscountEntities().clear();

        } else {
            // Case create
            discountEntity = modelMapper.map(discountRequestDTO, DiscountEntity.class);
            discountEntity.setCourseDiscountEntities(new HashSet<>());
            discountEntity.setCategoryDiscountEntities(new HashSet<>());
        }


        Set<CourseDiscountEntity> courseDiscountEntities = discountRequestDTO.getCourseIds().stream().map(courseId -> {
            CourseDiscountEntity courseDiscountEntity = new CourseDiscountEntity();
            courseDiscountEntity.setCourseEntity(courseService.findCourseEntityById(courseId));
            courseDiscountEntity.setDiscountEntity(discountEntity);
            return courseDiscountEntity;
        }).collect(Collectors.toSet());

        Set<CategoryDiscountEntity> categoryDiscountEntities = discountRequestDTO.getCategoryIds().stream().map(categoryId -> {
            CategoryDiscountEntity categoryDiscountEntity = new CategoryDiscountEntity();
            CategoryEntity categoryEntity = categoryService.findById(categoryId);
            categoryDiscountEntity.setCategoryEntity(categoryEntity);
            categoryDiscountEntity.setDiscountEntity(discountEntity);
            return categoryDiscountEntity;
        }).collect(Collectors.toSet());

        discountEntity.getCourseDiscountEntities().addAll(courseDiscountEntities);
        discountEntity.getCategoryDiscountEntities().addAll(categoryDiscountEntities);
        discountScheduler.updateDiscountStatus(discountEntity);
        return discountEntity;
    }


    public DiscountResponseDTO toDto(DiscountEntity discountEntity) {
        if (discountEntity == null) {
            return null;
        }
        return DiscountResponseDTO.builder()
                .id(discountEntity.getId())
                .percentage(discountEntity.getPercentage())
                .expiryDate(discountEntity.getEndDate())
                .isActive(discountEntity.getIsActive())
                .description(discountEntity.getDescription())
                .quantity(discountEntity.getQuantity())
                .build();
    }


    public List<DiscountResponseDTO> toDtoList(List<DiscountEntity> discountEntities) {
        return discountEntities.stream().map(this::toDto).toList();
    }

    public Page<DiscountSearchResponseDTO> toSearchResponseDTO(Page<DiscountEntity> discountEntities) {
        return discountEntities.map(discountEntity -> DiscountSearchResponseDTO.builder()
                .id(discountEntity.getId())
                .percentage(discountEntity.getPercentage())
                .startDate(discountEntity.getStartDate())
                .endDate(discountEntity.getEndDate())
                .isActive(discountEntity.getIsActive())
                .availableQuantity(discountEntity.getQuantity() - discountEntity.getUserDiscountEntities().size())
                .description(discountEntity.getDescription())
                .totalCategory(discountEntity.getCategoryDiscountEntities().size())
                .totalCourse(discountEntity.getCourseDiscountEntities().size())
                .courseIds(discountEntity.getCourseDiscountEntities().stream()
                        .map(courseDiscountEntity -> courseDiscountEntity.getCourseEntity().getId())
                        .toList())
                .categoryIds(discountEntity.getCategoryDiscountEntities().stream()
                        .map(categoryDiscountEntity -> categoryDiscountEntity.getCategoryEntity().getId())
                        .toList())
                .quantity(discountEntity.getQuantity())
                .usage(discountScheduler.getUsedDiscount(discountEntity))
                .status(discountEntity.getStatus())
                .build());
    }





}
