package com.coursehub.converter;

import com.coursehub.dto.response.announcement.AnnouncementResponseDTO;
import com.coursehub.entity.AnnouncementEntity;

public class AnnouncementConverter {
    public static AnnouncementResponseDTO toDto(AnnouncementEntity entity) {
        return new AnnouncementResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getType(),
                entity.getTargetGroup(),
                entity.getTargetGroup() != null ? entity.getTargetGroup().getDescription() : null,
                entity.getLink(),
                entity.getCreatedDate() != null ? entity.getCreatedDate().toString() : null
        );
    }
}