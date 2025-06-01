package com.coursehub.converter;

import com.coursehub.dto.request.review.ReviewRequestDTO;
import com.coursehub.dto.response.review.ReviewResponseDTO;
import com.coursehub.entity.ReviewEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ReviewConverter {
    
    private final ModelMapper modelMapper;
    
    public ReviewEntity toEntity(ReviewRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        
        ReviewEntity entity = modelMapper.map(requestDTO, ReviewEntity.class);
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
        
        modelMapper.map(requestDTO, entity);
    }
} 