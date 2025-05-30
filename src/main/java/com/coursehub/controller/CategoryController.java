package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.category.CategoryRequestDTO;
import com.coursehub.dto.response.category.CategoryResponseDTO;
import com.coursehub.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ResponseGeneral<Page<CategoryResponseDTO>>> findAll(
            @RequestParam(required = false) String name,
            Pageable pageable) {
        Page<CategoryResponseDTO> categoryResponseDTO = categoryService.findAll(name, pageable);
        ResponseGeneral<Page<CategoryResponseDTO>> response = new ResponseGeneral<>();
        response.setData(categoryResponseDTO);
        response.setMessage("Success");
        response.setDetail("Categories retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseGeneral<CategoryResponseDTO>> create(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO categoryResponseDTO = categoryService.create(requestDTO);
        ResponseGeneral<CategoryResponseDTO> response = new ResponseGeneral<>();
        response.setData(categoryResponseDTO);
        response.setMessage("Success");
        response.setDetail("Category created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseGeneral<CategoryResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO categoryResponseDTO = categoryService.update(id, requestDTO);
        ResponseGeneral<CategoryResponseDTO> response = new ResponseGeneral<>();
        response.setData(categoryResponseDTO);
        response.setMessage("Success");
        response.setDetail("Category updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGeneral<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage("Success");
        response.setDetail("Category deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 