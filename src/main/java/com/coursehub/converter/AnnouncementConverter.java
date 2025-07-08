package com.coursehub.converter;

import com.coursehub.dto.response.announcement.AnnouncementResponseDTO;
import com.coursehub.entity.AnnouncementEntity;
import com.coursehub.entity.AnnouncementUserReadEntity;

public class AnnouncementConverter {
    public static AnnouncementResponseDTO toDto(AnnouncementEntity entity, AnnouncementUserReadEntity userRead) {
        return new AnnouncementResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getType(),
                entity.getTargetGroup(),
                entity.getTargetGroup() != null ? entity.getTargetGroup().getDescription() : null,
                entity.getLink(),
                entity.getCreatedDate() != null ? entity.getCreatedDate().toString() : null,
                userRead != null ? userRead.getIsRead() : 0L,
                userRead != null ? userRead.getIsDeleted() : 0L
        );
    }

    public static AnnouncementResponseDTO toDto(AnnouncementEntity entity) {
        return toDto(entity, null);
    }
}