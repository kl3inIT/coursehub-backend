package com.coursehub.service;

import com.coursehub.dto.request.category.CategoryRequestDTO;
import com.coursehub.dto.response.category.CategoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryResponseDTO> findAll(String name, Pageable pageable);
    CategoryResponseDTO create(CategoryRequestDTO requestDTO);
    CategoryResponseDTO update(Long id, CategoryRequestDTO requestDTO);
    void delete(Long id);
}
