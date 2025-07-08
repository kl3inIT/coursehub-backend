package com.coursehub.converter;

import com.coursehub.dto.request.contact.FeedbackRequestDTO;
import com.coursehub.dto.response.contact.FeedbackResponseDTO;
import com.coursehub.entity.FeedbackEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedbackConverter {

    private final ModelMapper modelMapper;

    public FeedbackEntity toEntity(FeedbackRequestDTO dto) {
        return modelMapper.map(dto, FeedbackEntity.class);
    }

    public FeedbackResponseDTO toResponseDTO(FeedbackEntity entity) {
        return FeedbackResponseDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .subject(entity.getSubject())
                .message(entity.getMessage())
                .category(entity.getCategory().name())
                .adminReply(entity.getAdminReply())
                .createdAt(entity.getCreatedDate())
                .build();
    }

    public List<FeedbackResponseDTO> toResponseDTO(List<FeedbackEntity> entities) {
        return entities.stream().map(entity -> toResponseDTO(entity)).collect(Collectors.toList());
    }
}
