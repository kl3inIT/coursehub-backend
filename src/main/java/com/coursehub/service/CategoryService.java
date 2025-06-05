package com.coursehub.service;

import com.coursehub.dto.request.category.CategoryRequestDTO;
import com.coursehub.dto.response.category.CategoryResponseDTO;
import com.coursehub.dto.response.category.CategoryChartDTO;
import com.coursehub.dto.response.category.CategoryDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CategoryService {
    Page<CategoryResponseDTO> findAllOrNameCategories(String name, Pageable pageable);
    CategoryResponseDTO saveCategory(CategoryRequestDTO requestDTO);
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO);
    void deleteCategory(Long id);
    List<CategoryChartDTO> getCategoryChart();
    CategoryDetailDTO getCategoryDetail(Long categoryId);
    List<CategoryDetailDTO> getAllCategoryDetails();
}
