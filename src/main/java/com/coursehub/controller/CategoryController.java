package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.category.CategoryRequestDTO;
import com.coursehub.dto.response.category.CategoryResponseDTO;
import com.coursehub.entity.CategoryEntity;
import com.coursehub.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.coursehub.constant.Constant.CommonConstants.SUCCESS;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ResponseGeneral<Page<CategoryResponseDTO>>> getAllOrNameCategorise(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 6, sort = "id") Pageable pageable) {
        Page<CategoryResponseDTO> categoryResponseDTOS = categoryService.findAllOrNameCategories(name, pageable);
        ResponseGeneral<Page<CategoryResponseDTO>> response = new ResponseGeneral<>();
        response.setData(categoryResponseDTOS);
        response.setMessage(SUCCESS);
        response.setDetail("Categories retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseGeneral<CategoryResponseDTO>> getCategoryById(@PathVariable Long id) {
        CategoryResponseDTO categoryResponseDTO = categoryService.findDTOById(id);
        ResponseGeneral<CategoryResponseDTO> response = new ResponseGeneral<>();
        response.setData(categoryResponseDTO);
        response.setMessage(SUCCESS);
        response.setDetail("Category retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseGeneral<CategoryResponseDTO>> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO categoryResponseDTO = categoryService.saveCategory(requestDTO);
        ResponseGeneral<CategoryResponseDTO> response = new ResponseGeneral<>();
        response.setData(categoryResponseDTO);
        response.setMessage(SUCCESS);
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
        response.setMessage(SUCCESS);
        response.setDetail("Category updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGeneral<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage(SUCCESS);
        response.setDetail("Category deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

} 