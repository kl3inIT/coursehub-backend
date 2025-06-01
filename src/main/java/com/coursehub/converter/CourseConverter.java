package com.coursehub.converter;

import com.coursehub.dto.request.course.CourseRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.entity.CategoryEntity;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.EnrollmentEntity;
import com.coursehub.entity.ReviewEntity;
import com.coursehub.exception.category.CategoryNotFoundException;
import com.coursehub.exception.course.CourseNotFoundException;
import com.coursehub.repository.CategoryRepository;
import com.coursehub.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

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

        return courseResponseDTO;
    }

    public CourseEntity toEntity(CourseRequestDTO courseDTO) {
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

    private String generateThumbnailUrl(String thumbnailKey) {
        if (thumbnailKey == null || thumbnailKey.isEmpty()) {
            return null;
        }
        return s3Service.generatePermanentUrl(thumbnailKey);
    }

    private Double calculateAverageRating(Set<ReviewEntity> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }

        double average = reviews.stream()
                .mapToInt(ReviewEntity::getStar)
                .average()
                .orElse(0.0);

        return Math.round(average * 10.0) / 10.0;
    }

    private Long calculateTotalReviews(Set<ReviewEntity> reviews) {
        return reviews != null ? (long) reviews.size() : 0L;
    }

    private Long calculateTotalStudents(Set<EnrollmentEntity> enrollments) {
        return enrollments != null ? (long) enrollments.size() : 0L;
    }

    private Long calculateTotalLessons(Set<?> lessons) {
        return lessons != null ? (long) lessons.size() : 0L;
    }

    private Double calculateFinalPrice(BigDecimal price, BigDecimal discount) {
        if (price == null) {
            return 0.0;
        }

        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal finalPrice = price.subtract(discount);
            return finalPrice.compareTo(BigDecimal.ZERO) < 0 ? 0.0 : finalPrice.doubleValue();
        }

        return price.doubleValue();
    }


} 