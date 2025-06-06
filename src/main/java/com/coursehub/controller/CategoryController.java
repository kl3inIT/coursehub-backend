package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.category.CategoryRequestDTO;
import com.coursehub.dto.response.category.CategoryResponseDTO;
import com.coursehub.dto.response.category.CategoryChartDTO;
import com.coursehub.dto.response.category.CategoryDetailDTO;
import com.coursehub.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ResponseGeneral<Page<CategoryResponseDTO>>> getAllOrNameCategorise(
            @RequestParam(required = false) String name,
            Pageable pageable) {
        Page<CategoryResponseDTO> categoryResponseDTOS = categoryService.findAllOrNameCategories(name, pageable);
        ResponseGeneral<Page<CategoryResponseDTO>> response = new ResponseGeneral<>();
        response.setData(categoryResponseDTOS);
        response.setMessage("Success");
        response.setDetail("Categories retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseGeneral<CategoryResponseDTO>> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO categoryResponseDTO = categoryService.saveCategory(requestDTO);
        ResponseGeneral<CategoryResponseDTO> response = new ResponseGeneral<>();
        response.setData(categoryResponseDTO);
        response.setMessage("Success");
        response.setDetail("Category created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseGeneral<CategoryResponseDTO>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO categoryResponseDTO = categoryService.updateCategory(id, requestDTO);
        ResponseGeneral<CategoryResponseDTO> response = new ResponseGeneral<>();
        response.setData(categoryResponseDTO);
        response.setMessage("Success");
        response.setDetail("Category updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGeneral<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage("Success");
        response.setDetail("Category deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/chart")
    public ResponseEntity<List<CategoryChartDTO>> getCategoryChart() {
        return ResponseEntity.ok(categoryService.getCategoryChart());
    }

    @GetMapping("/{categoryId}/detail")
    public ResponseEntity<CategoryDetailDTO> getCategoryDetail(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryDetail(categoryId));
    }

    @GetMapping("/details")
    public ResponseEntity<List<CategoryDetailDTO>> getAllCategoryDetails() {
        return ResponseEntity.ok(categoryService.getAllCategoryDetails());
    }
} 