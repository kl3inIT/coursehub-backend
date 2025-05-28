package com.coursehub.converter;

import com.coursehub.dto.request.course.CourseRequestDTO;
import com.coursehub.dto.response.course.CourseResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.EnrollmentEntity;
import com.coursehub.entity.ReviewEntity;
import com.coursehub.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CourseConverter {

    private final ModelMapper modelMapper;
    private final S3Service s3Service;

    public CourseResponseDTO toResponseDTO(CourseEntity course) {
        if (course == null) {
            return null;
        }

        // Use ModelMapper for basic field mapping
        CourseResponseDTO dto = modelMapper.map(course, CourseResponseDTO.class);

        // Apply complex transformations
        dto.setThumbnailUrl(generateThumbnailUrl(course.getThumbnail()));
        dto.setInstructorName("CourseHub");
        dto.setAverageRating(calculateAverageRating(course.getReviews()));
        dto.setTotalReviews(calculateTotalReviews(course.getReviews()));
        dto.setTotalStudents(calculateTotalStudents(course.getEnrollments()));
        dto.setTotalLessons(calculateTotalLessons(course.getLessons()));
        dto.setFinalPrice(calculateFinalPrice(course.getPrice(), course.getDiscount()));

        return dto;
    }

    public CourseEntity toEntity(CourseRequestDTO courseDTO) {
        if (courseDTO == null) {
            return null;
        }

        // Use ModelMapper for basic field mapping
        CourseEntity entity = modelMapper.map(courseDTO, CourseEntity.class);
        // User will be set in the service layer
        // Set complex fields
        entity.setThumbnail(null);
        // Note: Instructor ID should be set separately in service layer

        return entity;
    }

    public List<CourseResponseDTO> toResponseDTOList(List<CourseEntity> courses) {
        return courses.stream()
                .map(this::toResponseDTO)
                .toList();
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