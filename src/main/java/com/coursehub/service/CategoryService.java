package com.coursehub.service;

import com.coursehub.dto.request.category.CategoryRequestDTO;
import com.coursehub.dto.response.category.CategoryResponseDTO;
import com.coursehub.dto.response.analytics.CategoryAnalyticsChartResponseDTO;
import com.coursehub.dto.response.analytics.CategoryAnalyticsDetailResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CategoryService {
    Page<CategoryResponseDTO> findAllOrNameCategories(String name, Pageable pageable);
    CategoryResponseDTO saveCategory(CategoryRequestDTO requestDTO);
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO);
    void deleteCategory(Long id);
    List<CategoryAnalyticsChartResponseDTO> getCategoryChart();
    CategoryAnalyticsDetailResponseDTO getCategoryDetail(Long categoryId);
    List<CategoryAnalyticsDetailResponseDTO> getAllCategoryDetails();
}
