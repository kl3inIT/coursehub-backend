package com.coursehub.converter;

import com.coursehub.dto.response.announcement.AnnouncementResponseDTO;
import com.coursehub.entity.AnnouncementEntity;
import com.coursehub.entity.AnnouncementUserReadEntity;
import com.coursehub.repository.UserRepository;
import com.coursehub.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@AllArgsConstructor
@RequiredArgsConstructor
@Component
public class AnnouncementConverter {
    @Autowired
    private UserRepository userRepository;

    public AnnouncementResponseDTO toDto(AnnouncementEntity entity, AnnouncementUserReadEntity userRead) {
        String createdByName = null;
        if (entity.getCreatedBy() != null) {
            UserEntity user = userRepository.findById(entity.getCreatedBy()).orElse(null);
            if (user != null) {
                createdByName = user.getName();
            }
        }
        long isRead = (userRead != null) ? 1 : 0;
        LocalDateTime sentTime = entity.getSentTime();
        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
        Date sentTimeAsDate = Date.from(sentTime.atZone(zone).toInstant());
        AnnouncementResponseDTO dto = new AnnouncementResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(sentTimeAsDate);
        dto.setScheduledTime(entity.getScheduledTime());
        dto.setSentTime(entity.getSentTime());
        dto.setCreatedByName(createdByName);
        dto.setTargetGroup(entity.getTargetGroup());
        dto.setIsRead(isRead);
        dto.setTargetGroup(
                entity.getTargetGroup() != null ? entity.getTargetGroup() : null
        );
        dto.setType(entity.getType());
        dto.setLink(entity.getLink());
        dto.setUpdatedAt(sentTimeAsDate);
        return dto;
    }

    public AnnouncementResponseDTO toDto(AnnouncementEntity entity) {
        return toDto(entity, null);
    }
}