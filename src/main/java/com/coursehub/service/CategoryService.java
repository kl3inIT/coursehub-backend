package com.coursehub.service;

import com.coursehub.dto.request.category.CategoryRequestDTO;
import com.coursehub.dto.response.category.CategoryResponseDTO;
import com.coursehub.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryResponseDTO> findAllOrNameCategories(String name, Pageable pageable);
    CategoryResponseDTO saveCategory(CategoryRequestDTO requestDTO);
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO);
    void deleteCategory(Long id);

    CategoryEntity findById(Long id);
}
