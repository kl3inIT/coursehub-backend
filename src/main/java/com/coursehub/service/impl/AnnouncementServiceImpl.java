package com.coursehub.service.impl;

import com.coursehub.converter.AnnouncementConverter;
import com.coursehub.dto.request.announcement.AnnouncementCreateRequestDTO;
import com.coursehub.dto.response.announcement.AnnouncementResponseDTO;
import com.coursehub.dto.response.announcement.TargetGroupDTO;
import com.coursehub.entity.AnnouncementEntity;
import com.coursehub.entity.AnnouncementUserReadEntity;
import com.coursehub.enums.NotificationType;
import com.coursehub.enums.TargetGroup;
import com.coursehub.exceptions.announcement.AnnouncementNotFoundException;
import com.coursehub.repository.AnnouncementRepository;
import com.coursehub.repository.AnnouncementUserReadRepository;
import com.coursehub.service.AnnouncementService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    private final AnnouncementUserReadRepository announcementUserReadRepository;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository, AnnouncementUserReadRepository announcementUserReadRepository) {
        this.announcementRepository = announcementRepository;
        this.announcementUserReadRepository = announcementUserReadRepository;
    }

    @Override
    public AnnouncementResponseDTO createAnnouncement(AnnouncementCreateRequestDTO dto, String adminId) {
        AnnouncementEntity announcement = new AnnouncementEntity();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setTargetGroup(dto.getTargetGroup());
        announcement.setLink(dto.getLink());
        announcement.setType(NotificationType.ADMIN_ANNOUNCEMENT);
        AnnouncementEntity saved = announcementRepository.save(announcement);
        return AnnouncementConverter.toDto(saved);
    }

    @Override
    public List<AnnouncementResponseDTO> getAnnouncements(TargetGroup targetGroup) {
        List<AnnouncementEntity> entities =
                (targetGroup == null || targetGroup == TargetGroup.ALL)
                        ? announcementRepository.findAll()
                        : announcementRepository.findByTargetGroupIn(Arrays.asList(TargetGroup.ALL, targetGroup));
        return entities.stream()
                .map(AnnouncementConverter::toDto)
                .toList();
    }

    @Override
    public AnnouncementResponseDTO getAnnouncement(Long id) {
        AnnouncementEntity entity = announcementRepository.findById(id)
                .orElseThrow(() -> new AnnouncementNotFoundException("Announcement not found!"));
        return AnnouncementConverter.toDto(entity);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    @Override
    public void markAsRead(Long announcementId, Long userId) {
        if (!announcementUserReadRepository.existsByAnnouncementIdAndUserId(announcementId, userId)) {
            AnnouncementUserReadEntity read = new AnnouncementUserReadEntity();
            read.setAnnouncement(announcementRepository.getReferenceById(announcementId));
            read.setUserId(userId);
            read.setIsRead(1L);
            announcementUserReadRepository.save(read);
        }
    }

    @Override
    public List<TargetGroupDTO> getAllTargetGroups() {
        return Arrays.stream(TargetGroup.values())
                .map(tg -> new TargetGroupDTO(tg.name(), tg.getDescription()))
                .toList();
    }
}
