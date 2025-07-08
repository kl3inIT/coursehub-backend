package com.coursehub.service.impl;

import com.coursehub.converter.AnnouncementConverter;
import com.coursehub.dto.request.announcement.AnnouncementCreateRequestDTO;
import com.coursehub.dto.response.announcement.AnnouncementResponseDTO;
import com.coursehub.dto.response.announcement.TargetGroupDTO;
import com.coursehub.entity.AnnouncementEntity;
import com.coursehub.entity.AnnouncementUserReadEntity;
import com.coursehub.entity.RoleEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.NotificationType;
import com.coursehub.enums.TargetGroup;
import com.coursehub.exceptions.announcement.AnnouncementNotFoundException;
import com.coursehub.repository.AnnouncementRepository;
import com.coursehub.repository.AnnouncementUserReadRepository;
import com.coursehub.service.AnnouncementService;
import com.coursehub.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    private final AnnouncementUserReadRepository announcementUserReadRepository;

    private final UserService userService;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository, AnnouncementUserReadRepository announcementUserReadRepository, UserService userService) {
        this.announcementRepository = announcementRepository;
        this.announcementUserReadRepository = announcementUserReadRepository;
        this.userService = userService;
    }

    @Override
    public AnnouncementResponseDTO createAnnouncement(AnnouncementCreateRequestDTO dto) {
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
    public void markAsRead(Long announcementId) {
        UserEntity user = userService.getCurrentUser();
        if (!announcementUserReadRepository.existsByAnnouncementIdAndUserId(announcementId, user.getId())) {
            AnnouncementUserReadEntity read = new AnnouncementUserReadEntity();
            read.setAnnouncement(announcementRepository.getReferenceById(announcementId));
            read.setUserId(user.getId());
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

    @Override
    public void markAsDeleted(Long announcementId) {
        UserEntity user = userService.getCurrentUser();
        AnnouncementUserReadEntity entity = announcementUserReadRepository
                .findByAnnouncementIdAndUserId(announcementId, user.getId())
                .orElseGet(() -> {
                    AnnouncementUserReadEntity newEntity = new AnnouncementUserReadEntity();
                    newEntity.setAnnouncement(announcementRepository.getReferenceById(announcementId));
                    newEntity.setUserId(user.getId());
                    newEntity.setIsRead(0L);
                    newEntity.setIsDeleted(0L);
                    return newEntity;
                });
        entity.setIsDeleted(1L);
        announcementUserReadRepository.save(entity);
    }

    @Override
    public List<AnnouncementResponseDTO> getAnnouncementsForCurrentUser() {
        UserEntity user = userService.getCurrentUser();
        List<TargetGroup> targetGroups = getTargetGroups(user);

        // Lấy announcements phù hợp
        List<AnnouncementEntity> announcements = announcementRepository.findByTargetGroupIn(targetGroups);

        // Lấy map các thông tin read/deleted của user với từng announcement
        Map<Long, AnnouncementUserReadEntity> userReadMap =
                announcementUserReadRepository.findByUserId(user.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                e -> e.getAnnouncement().getId(),
                                e -> e
                        ));

        // Lọc ra các thông báo chưa bị xóa và convert sang DTO
        return announcements.stream()
                .filter(a -> {
                    AnnouncementUserReadEntity read = userReadMap.get(a.getId());
                    return read == null || read.getIsDeleted() == null || read.getIsDeleted() == 0L;
                })
                .map(a -> AnnouncementConverter.toDto(a, userReadMap.get(a.getId())))
                .toList();
    }

    private static List<TargetGroup> getTargetGroups(UserEntity user) {
        RoleEntity role = user.getRoleEntity();
        String roleCode = role.getCode().toUpperCase();

        List<TargetGroup> targetGroups;
        if (roleCode.equals("LEARNER")) {
            targetGroups = Arrays.asList(TargetGroup.ALL, TargetGroup.LEARNER);
        } else if (roleCode.equals("MANAGER")) {
            targetGroups = Arrays.asList(TargetGroup.ALL, TargetGroup.MANAGER);
        } else if (roleCode.equals("ADMIN")) {
            targetGroups = List.of(TargetGroup.ALL);
        } else {
            targetGroups = List.of(TargetGroup.ALL);
        }
        return targetGroups;
    }

}
