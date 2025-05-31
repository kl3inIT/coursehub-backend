package com.coursehub.service.impl;

import com.coursehub.converter.CategoryConverter;
import com.coursehub.dto.request.category.CategoryRequestDTO;
import com.coursehub.dto.response.category.CategoryResponseDTO;
import com.coursehub.entity.CategoryEntity;
import com.coursehub.exception.category.CategoryNotFoundException;
import com.coursehub.exception.category.CategoryUsingException;
import com.coursehub.repository.CategoryRepository;
import com.coursehub.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;

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
}
