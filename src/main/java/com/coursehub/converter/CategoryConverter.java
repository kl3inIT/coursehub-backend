package com.coursehub.converter;

import com.coursehub.dto.request.category.CategoryRequestDTO;
import com.coursehub.dto.response.category.CategoryResponseDTO;
import com.coursehub.entity.CategoryEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryConverter {
    
    private final ModelMapper modelMapper;

    // RequestDTO => Entity
    public CategoryEntity toEntity(CategoryRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        return modelMapper.map(requestDTO, CategoryEntity.class);
    }

    // Entity => ResponseDTO
    public CategoryResponseDTO toResponseDTO(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }

        // Map toàn bộ trường mặc định
        CategoryResponseDTO responseDTO = modelMapper.map(entity, CategoryResponseDTO.class);
        responseDTO.setCourseCount((long)entity.getCourseEntities().size());
        return responseDTO;
    }

    // List Entity => List ResponseDTO
    public List<CategoryResponseDTO> toResponseDTOList(List<CategoryEntity> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // Update Entity from RequestDTO
    public CategoryEntity updateEntity(CategoryEntity entity, CategoryRequestDTO requestDTO) {
        if (entity == null || requestDTO == null) {
            return null;
        }
        modelMapper.map(requestDTO, entity);
        return entity;
    }
} 