package com.coursehub.converter;

import com.coursehub.dto.request.review.ReviewRequestDTO;
import com.coursehub.dto.response.review.ReviewResponseDTO;
import com.coursehub.entity.ReviewEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class ReviewConverter {
    
    private final ModelMapper modelMapper;
    
    public ReviewEntity toEntity(ReviewRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        ReviewEntity entity = new ReviewEntity();
        entity.setStar(requestDTO.getStar());
        entity.setComment(requestDTO.getComment());
        return entity;
    }
    
    public ReviewResponseDTO toResponseDTO(ReviewEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ReviewResponseDTO responseDTO = modelMapper.map(entity, ReviewResponseDTO.class);
        
        // Map các trường từ các entity liên quan
        if (entity.getUserEntity() != null) {
            responseDTO.setUserId(entity.getUserEntity().getId());
            responseDTO.setUserName(entity.getUserEntity().getName());
            responseDTO.setUserAvatar(entity.getUserEntity().getAvatar());
        }
        responseDTO.setIsHidden(entity.getIsHidden());
        if (entity.getCourseEntity() != null) {
            responseDTO.setCourseId(entity.getCourseEntity().getId());
            responseDTO.setCourseName(entity.getCourseEntity().getTitle());
            responseDTO.setCategoryName(entity.getCourseEntity().getCategoryEntity().getName());
        }
        
        // Map createdDate và modifiedDate
        if (entity.getCreatedDate() != null) {
            responseDTO.setCreatedDate(entity.getCreatedDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        if (entity.getModifiedDate() != null) {
            responseDTO.setModifiedDate(entity.getModifiedDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        
        return responseDTO;
    }
    
    public void updateEntity(ReviewEntity entity, ReviewRequestDTO requestDTO) {
        if (entity == null || requestDTO == null) {
            return;
        }
        entity.setStar(requestDTO.getStar());
        entity.setComment(requestDTO.getComment());
    }
} 