package com.coursehub.converter;

import com.coursehub.dto.request.review.ReviewRequestDTO;
import com.coursehub.dto.response.review.ReviewResponseDTO;
import com.coursehub.entity.ReviewEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewConverter {
    
    private final ModelMapper modelMapper;
    
    public ReviewEntity toEntity(ReviewRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        
        ReviewEntity entity = modelMapper.map(requestDTO, ReviewEntity.class);
        entity.setIsActive(true);
        return entity;
    }
    
    public ReviewResponseDTO toResponseDTO(ReviewEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ReviewResponseDTO responseDTO = modelMapper.map(entity, ReviewResponseDTO.class);
        
        // Map các trường từ các entity liên quan
        responseDTO.setUserId(entity.getUserEntity().getId());
        responseDTO.setUserName(entity.getUserEntity().getName());
        responseDTO.setUserAvatar(entity.getUserEntity().getAvatar());
        responseDTO.setCourseId(entity.getCourseEntity().getId());
        responseDTO.setCourseName(entity.getCourseEntity().getTitle());
        
        return responseDTO;
    }
    
    public void updateEntity(ReviewEntity entity, ReviewRequestDTO requestDTO) {
        if (entity == null || requestDTO == null) {
            return;
        }
        
        modelMapper.map(requestDTO, entity);
    }
} 