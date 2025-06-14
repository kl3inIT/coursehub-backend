package com.coursehub.service.impl;

import com.coursehub.converter.CategoryConverter;
import com.coursehub.dto.request.category.CategoryRequestDTO;
import com.coursehub.dto.response.category.CategoryResponseDTO;
import com.coursehub.entity.CategoryEntity;
import com.coursehub.exceptions.category.CategoryNotFoundException;
import com.coursehub.exceptions.category.CategoryUsingException;
import com.coursehub.repository.CategoryRepository;
import com.coursehub.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coursehub.dto.response.category.CategoryChartDTO;
import com.coursehub.dto.response.category.CategoryDetailDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    public Page<CategoryResponseDTO> findAllOrNameCategories(String name, Pageable pageable) {
        return categoryRepository.findAll(name, pageable)
                .map(categoryConverter::toResponseDTO);
    }

    @Override
    @Transactional
    public CategoryResponseDTO saveCategory(CategoryRequestDTO requestDTO) {
        CategoryEntity entity = categoryConverter.toEntity(requestDTO);
        return categoryConverter.toResponseDTO(categoryRepository.save(entity));
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        
        categoryConverter.updateEntity(entity, requestDTO);
        return categoryConverter.toResponseDTO(categoryRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        if (!category.getCourseEntities().isEmpty()) {
            throw new CategoryUsingException("Cannot delete category with id: " + id + " because it is being used by courses");
        }

        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryChartDTO> getCategoryChart() {
        List<Object[]> results = categoryRepository.getCategoryCourseCounts();
        long total = results.stream().mapToLong(r -> (Long) r[1]).sum();
        return results.stream()
            .map(r -> new CategoryChartDTO(
                (String) r[0],
                (Long) r[1],
                total == 0 ? 0.0 : ((double) (Long) r[1] / total) * 100
            ))
            .toList();
    }

    @Override
    public CategoryDetailDTO getCategoryDetail(Long categoryId) {
        List<Object[]> results = categoryRepository.getCategoryDetail(categoryId);
        if (results == null || results.isEmpty()) {
            return null;
        }
        Object[] r = results.get(0);
        log.debug("CategoryDetail array: {}", (Object) r);
        return new CategoryDetailDTO(
            r[0] != null ? ((Number) r[0]).longValue() : null,           // categoryId
            (String) r[1],                                               // categoryName
            (String) r[2],                                               // description
            r[3] != null ? ((Number) r[3]).longValue() : 0L,             // courseCount
            r[4] != null ? ((Number) r[4]).doubleValue() : 0.0,          // averageRating
            r[5] != null ? ((Number) r[5]).longValue() : 0L,             // totalStudents
            r[6] != null ? (java.math.BigDecimal) r[6] : java.math.BigDecimal.ZERO, // totalRevenue
            (java.util.Date) r[7],                                       // createdDate
            (java.util.Date) r[8]                                        // modifiedDate
        );
    }

    @Override
    public List<CategoryDetailDTO> getAllCategoryDetails() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        return categories.stream().map(category -> {
            Long courseCount = (long) category.getCourseEntities().size();
            double totalRating = 0.0;
            long totalReviews = 0L;
            long totalStudents = 0L;
            java.math.BigDecimal totalRevenue = java.math.BigDecimal.ZERO; // set = 0 chưa có transaction
            for (var course : category.getCourseEntities()) {
                // Tính rating trung bình
                if (course.getReviewEntities() != null && !course.getReviewEntities().isEmpty()) {
                    totalRating += course.getReviewEntities().stream().mapToInt(r -> r.getStar()).sum();
                    totalReviews += course.getReviewEntities().size();
                }
                // Tính tổng học viên
                if (course.getEnrollmentEntities() != null) {
                    totalStudents += course.getEnrollmentEntities().size();
                }
                // Tính tổng doanh thu
                // if (course.getPaymentEntities() != null) {
                //     for (var payment : course.getPaymentEntities()) {
                //         if (payment.getAmount() != null) {
                //             totalRevenue = totalRevenue.add(payment.getAmount());
                //         }
                //     }
                // }
            }
            double averageRating = totalReviews > 0 ? totalRating / totalReviews : 0.0;
            return new CategoryDetailDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                courseCount,
                averageRating,
                totalStudents,
                totalRevenue,
                category.getCreatedDate(),
                category.getModifiedDate()
            );
        }).toList();
    }

    @Override
    public CategoryEntity findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }
}
