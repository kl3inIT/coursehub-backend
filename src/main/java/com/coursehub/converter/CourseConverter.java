package com.coursehub.converter;

import com.coursehub.dto.request.course.CourseCreationRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.entity.CategoryEntity;
import com.coursehub.entity.CourseEntity;
import com.coursehub.exceptions.category.CategoryNotFoundException;
import com.coursehub.exceptions.course.CourseNotFoundException;
import com.coursehub.repository.CategoryRepository;
import com.coursehub.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseConverter {

    private final ModelMapper modelMapper;
    private final S3Service s3Service;
    private final CategoryRepository categoryRepository;

    public CourseResponseDTO toResponseDTO(CourseEntity courseEntity) {
        if (courseEntity == null) {
            throw new CourseNotFoundException("Course not found");
        }

        // Use ModelMapper for basic field mapping
        CourseResponseDTO courseResponseDTO = modelMapper.map(courseEntity, CourseResponseDTO.class);

        String category = courseEntity.getCategoryEntity().getName();

        courseResponseDTO.setCategory(category);
        courseResponseDTO.setThumbnailUrl(generateThumbnailUrl(courseEntity.getThumbnail()));
        courseResponseDTO.setInstructorName("CourseHub Team"); // Assuming instructor is always "CourseHub Team"
        courseResponseDTO.setFinalPrice(calculateFinalPrice(courseEntity));
        return courseResponseDTO;
    }

    public CourseEntity toEntity(CourseCreationRequestDTO courseDTO) {
        if (courseDTO == null) {
            return null;
        }

        CategoryEntity categoryEntity = categoryRepository.findById(courseDTO.getCategoryCode()).orElseThrow(
                () -> new CategoryNotFoundException("Category not found")
        );
        CourseEntity courseEntity = modelMapper.map(courseDTO, CourseEntity.class);
        courseEntity.setCategoryEntity(categoryEntity);
        return courseEntity;
    }

    public List<CourseResponseDTO> toResponseDTOList(List<CourseEntity> courses) {
        return courses.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Page<CourseResponseDTO> toResponseDTOPage(Page<CourseEntity> courses) {
        return courses.map(this::toResponseDTO);
    }

    private BigDecimal calculateFinalPrice(CourseEntity courseEntity) {
        BigDecimal price = courseEntity.getPrice();
        BigDecimal discount = courseEntity.getDiscount();
        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountAmount = price.multiply(discount).divide(BigDecimal.valueOf(100));
            return price.subtract(discountAmount);
        }

        return price; // No discounts applied
    }

    private String generateThumbnailUrl(String thumbnailKey) {
        if (thumbnailKey == null || thumbnailKey.isEmpty()) {
            return null;
        }
        return s3Service.generatePermanentUrl(thumbnailKey);
    }

} 