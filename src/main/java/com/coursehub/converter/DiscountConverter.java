package com.coursehub.converter;

import com.coursehub.dto.request.discount.DiscountRequestDTO;
import com.coursehub.dto.response.discount.DiscountResponseDTO;
import com.coursehub.dto.response.discount.DiscountSearchResponseDTO;
import com.coursehub.entity.CategoryDiscountEntity;
import com.coursehub.entity.CategoryEntity;
import com.coursehub.entity.CourseDiscountEntity;
import com.coursehub.entity.DiscountEntity;
import com.coursehub.service.CategoryService;
import com.coursehub.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiscountConverter {

    private final ModelMapper modelMapper;
    private final CourseService courseService;
    private final CategoryService categoryService;

    public DiscountEntity toEntity(DiscountRequestDTO discountRequestDTO) {
        DiscountEntity discountEntity = modelMapper.map(discountRequestDTO, DiscountEntity.class);
        discountEntity.setCode(discountRequestDTO.getCode().trim().toUpperCase());

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

        discountEntity.getCourseDiscountEntities().clear();
        discountEntity.getCategoryDiscountEntities().clear();

        discountEntity.setCourseDiscountEntities(courseDiscountEntities);
        discountEntity.setCategoryDiscountEntities(categoryDiscountEntities);
        return discountEntity;
    }

    public DiscountResponseDTO toDto(DiscountEntity discountEntity) {
        if (discountEntity == null) {
            return null;
        }
        return DiscountResponseDTO.builder()
                .id(discountEntity.getId())
                .code(discountEntity.getCode())
                .percentage(discountEntity.getPercentage())
                .expiryDate(discountEntity.getExpiryDate())
                .isActive(discountEntity.getIsActive())
                .isGlobal(discountEntity.getIsGlobal())
                .description(discountEntity.getDescription())
                .quantity(discountEntity.getQuantity())
                .build();
    }

    public Page<DiscountSearchResponseDTO> toSearchResponseDTO(Page<DiscountEntity> discountEntities) {
        return discountEntities.map(discountEntity -> DiscountSearchResponseDTO.builder()
                .id(discountEntity.getId())
                .code(discountEntity.getCode())
                .percentage(discountEntity.getPercentage())
                .expiryTime(discountEntity.getExpiryDate())
                .isActive(discountEntity.getIsActive())
                .isGlobal(discountEntity.getIsGlobal())
                .description(discountEntity.getDescription())
                .totalCategory(discountEntity.getCategoryDiscountEntities().size())
                .totalCourse(discountEntity.getCourseDiscountEntities().size())
                .courseIds(discountEntity.getCourseDiscountEntities().stream()
                        .map(courseDiscountEntity -> courseDiscountEntity.getCourseEntity().getId())
                        .toList())
                .categoryIds(discountEntity.getCategoryDiscountEntities().stream()
                        .map(categoryDiscountEntity -> categoryDiscountEntity.getCategoryEntity().getId())
                        .toList())
                .usage(discountEntity.getPaymentEntities().size() + "/" + discountEntity.getQuantity())
                .build());
    }


}
