package com.coursehub.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.coursehub.exceptions.announcement.ContentTooLongException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coursehub.converter.AnnouncementConverter;
import com.coursehub.dto.request.announcement.AnnouncementCreateRequestDTO;
import com.coursehub.dto.response.announcement.AnnouncementResponseDTO;
import com.coursehub.dto.response.announcement.TargetGroupDTO;
import com.coursehub.entity.AnnouncementEntity;
import com.coursehub.entity.AnnouncementUserReadEntity;
import com.coursehub.entity.RoleEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.AnnouncementStatus;
import com.coursehub.enums.AnnouncementType;
import com.coursehub.enums.TargetGroup;
import com.coursehub.exceptions.announcement.AnnouncementNotFoundException;
import com.coursehub.exceptions.announcement.InternalServerException;
import com.coursehub.repository.AnnouncementRepository;
import com.coursehub.repository.AnnouncementUserReadRepository;
import com.coursehub.service.AnnouncementService;
import com.coursehub.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementUserReadRepository announcementUserReadRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final AnnouncementConverter announcementConverter;

    private AnnouncementEntity findAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new AnnouncementNotFoundException("Announcement not found!"));
    }

    @Override
    public AnnouncementResponseDTO createAnnouncement(AnnouncementCreateRequestDTO dto) {
        AnnouncementEntity announcement = new AnnouncementEntity();
        if(dto.getTitle().length() > 500) {
            throw new ContentTooLongException("Title cannot exceed 500 characters");
        }
        if(dto.getContent().length() > 5000) {
            throw new ContentTooLongException("Content cannot exceed 5000 characters");
        }
        if(dto.getLink() != null && dto.getLink().length() > 500) {
            throw new ContentTooLongException("Link cannot exceed 500 characters");
        }
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setType(dto.getType() != null ? dto.getType() : AnnouncementType.GENERAL);
        announcement.setTargetGroup(dto.getTargetGroup());
        announcement.setLink(dto.getLink());
        announcement.setScheduledTime(dto.getScheduledTime());

        if (dto.getStatus() != null) {
            announcement.setStatus(dto.getStatus());
        } else if (dto.getScheduledTime() != null && dto.getScheduledTime().isAfter(now)) {
            announcement.setStatus(AnnouncementStatus.SCHEDULED);
        } else {
            announcement.setStatus(AnnouncementStatus.DRAFT);
        }

        if (dto.getStatus() == AnnouncementStatus.SENT) {
            announcement.setSentTime(now);
        } else if (dto.getStatus() == AnnouncementStatus.SCHEDULED && dto.getScheduledTime() != null) {
            announcement.setSentTime(dto.getScheduledTime());
        }

        UserEntity currentUser = userService.getCurrentUser();
        announcement.setCreatedBy(currentUser.getId());
        AnnouncementEntity saved = announcementRepository.save(announcement);
        AnnouncementResponseDTO responseDTO = announcementConverter.toDto(saved);

        if (saved.getStatus() == AnnouncementStatus.SENT) {
            sendRealtimeNotification(saved, responseDTO);
        }

        return responseDTO;
    }

    @Override
    public Page<AnnouncementResponseDTO> getAllAnnouncements(
        AnnouncementType type,
        List<AnnouncementStatus> statuses,
        TargetGroup targetGroup,
        String search,
        String startDate,
        String endDate,
        Pageable pageable
    ) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime start = null;
        LocalDateTime end = null;
        if ((startDate == null || startDate.isEmpty()) || (endDate == null || endDate.isEmpty())) {
            // Mặc định 7 ngày gần nhất
            LocalDate today = LocalDate.now();
            LocalDate sevenDaysAgo = today.minusDays(6);
            start = sevenDaysAgo.atStartOfDay();
            end = today.atTime(23, 59, 59);
        } else {
            if (startDate != null) {
                start = LocalDate.parse(startDate, fmt).atStartOfDay();
            }
            if (endDate != null) {
                end = LocalDate.parse(endDate, fmt).atTime(23, 59, 59);
            }
        }
        Page<AnnouncementEntity> entities = announcementRepository.filterAnnouncements(
            type, statuses, targetGroup, search, start, end, pageable);
        return entities.map(announcementConverter::toDto);
    }

    @Override
    public AnnouncementResponseDTO getAnnouncement(Long id) {
        AnnouncementEntity entity = findAnnouncementById(id);
        return announcementConverter.toDto(entity);
    }

    @Override
    public AnnouncementResponseDTO updateAnnouncement(Long id, AnnouncementCreateRequestDTO dto) {
        AnnouncementEntity entity = findAnnouncementById(id);
        if(dto.getTitle().length() > 500) {
            throw new ContentTooLongException("Title cannot exceed 500 characters");
        }
        if(dto.getContent().length() > 5000) {
            throw new ContentTooLongException("Content cannot exceed 5000 characters");
        }
        if(dto.getLink().length() > 500) {
            throw new ContentTooLongException("Link cannot exceed 500 characters");
        }
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setType(dto.getType() != null ? dto.getType() : entity.getType());
        entity.setTargetGroup(dto.getTargetGroup());
        entity.setLink(dto.getLink());
        entity.setScheduledTime(dto.getScheduledTime());

        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        } else if (dto.getScheduledTime() != null && dto.getScheduledTime().isAfter(LocalDateTime.now())) {
            entity.setStatus(AnnouncementStatus.SCHEDULED);
        } else if (entity.getStatus() == AnnouncementStatus.SCHEDULED) {
            entity.setStatus(AnnouncementStatus.DRAFT);
        }
        
        AnnouncementEntity saved = announcementRepository.save(entity);
        return announcementConverter.toDto(saved);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    @Override
    public AnnouncementResponseDTO sendAnnouncement(Long id) {
        AnnouncementEntity entity = findAnnouncementById(id);
        
        if (entity.getStatus() != AnnouncementStatus.DRAFT && entity.getStatus() != AnnouncementStatus.SCHEDULED) {
            throw new InternalServerException("Announcement cannot be sent. Current status: " + entity.getStatus());
        }
        
        entity.setStatus(AnnouncementStatus.SENT);
        entity.setSentTime(LocalDateTime.now());
        
        AnnouncementEntity saved = announcementRepository.save(entity);
        AnnouncementResponseDTO responseDTO = announcementConverter.toDto(saved);

        sendRealtimeNotification(saved, responseDTO);
        
        return responseDTO;
    }

    @Override
    public AnnouncementResponseDTO scheduleAnnouncement(Long id, String scheduledTime) {
        AnnouncementEntity entity = findAnnouncementById(id);

        if (entity.getStatus() != AnnouncementStatus.DRAFT) {
            throw new InternalServerException("Only draft announcements can be scheduled");
        }

        entity.setStatus(AnnouncementStatus.SCHEDULED);
        LocalDateTime scheduled = null;
        if (scheduledTime != null) {
            scheduled = Instant.parse(scheduledTime)
                    .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                    .toLocalDateTime();
        }
        entity.setScheduledTime(scheduled);
        entity.setSentTime(scheduled);

        AnnouncementEntity saved = announcementRepository.save(entity);
        return announcementConverter.toDto(saved);
    }

    @Override
    public AnnouncementResponseDTO saveAsDraft(Long id) {
        AnnouncementEntity entity = findAnnouncementById(id);
        
        entity.setStatus(AnnouncementStatus.DRAFT);
        entity.setScheduledTime(null);
        entity.setSentTime(null); // Xóa sentTime khi chuyển về draft
        
        AnnouncementEntity saved = announcementRepository.save(entity);
        return announcementConverter.toDto(saved);
    }

    @Override
    public AnnouncementResponseDTO cancelAnnouncement(Long id) {
        AnnouncementEntity entity = findAnnouncementById(id);
        
        if (entity.getStatus() != AnnouncementStatus.SCHEDULED) {
            throw new InternalServerException("Only scheduled announcements can be cancelled");
        }
        
        entity.setStatus(AnnouncementStatus.CANCELLED);
        
        AnnouncementEntity saved = announcementRepository.save(entity);
        return announcementConverter.toDto(saved);
    }

    @Override
    public AnnouncementResponseDTO cloneAnnouncement(Long id) {
        AnnouncementEntity entity = findAnnouncementById(id);

        AnnouncementEntity clone = new AnnouncementEntity();
        clone.setTitle(entity.getTitle());
        clone.setContent(entity.getContent());
        clone.setType(entity.getType());
        clone.setTargetGroup(entity.getTargetGroup());
        clone.setLink(entity.getLink());
        clone.setScheduledTime(entity.getScheduledTime());
        clone.setStatus(AnnouncementStatus.DRAFT);
        clone.setCreatedBy(userService.getCurrentUser().getId());

        AnnouncementEntity savedClone = announcementRepository.save(clone);
        return announcementConverter.toDto(savedClone);
    }

    @Override
    public void markAsRead(Long announcementId) {
        UserEntity user = userService.getCurrentUser();
        if (!announcementUserReadRepository.existsByAnnouncementIdAndUserId(announcementId, user.getId())) {
            AnnouncementUserReadEntity read = new AnnouncementUserReadEntity();
            read.setAnnouncement(announcementRepository.getReferenceById(announcementId));
            read.setUser(user);
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
                    newEntity.setUser(user);
                    newEntity.setIsRead(0L);
                    newEntity.setIsDeleted(0L);
                    return newEntity;
                });
        entity.setIsDeleted(1L);
        announcementUserReadRepository.save(entity);
    }

    @Override
    public void restoreAnnouncement(Long id) {
        AnnouncementEntity entity = findAnnouncementById(id);
        entity.setStatus(AnnouncementStatus.SENT);
        announcementRepository.save(entity);
    }

    @Override
    public void archiveAnnouncement(Long id) {
        AnnouncementEntity entity = findAnnouncementById(id);

        if (entity.getStatus() != AnnouncementStatus.SENT) {
            throw new InternalServerException("Only sent announcements can be archived");
        }
        
        entity.setStatus(AnnouncementStatus.HIDDEN);
        announcementRepository.save(entity);
    }

    @Override
    public List<AnnouncementResponseDTO> getAnnouncementsForCurrentUser() {
        UserEntity user = userService.getCurrentUser();
        List<TargetGroup> targetGroups = getTargetGroups(user);

        List<AnnouncementEntity> announcements = announcementRepository.findByTargetGroupInAndStatus(targetGroups, AnnouncementStatus.SENT);

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
                .map(a -> announcementConverter.toDto(a, userReadMap.get(a.getId())))
                .toList();
    }

    @Override
    public Map<String, Integer> getAnnouncementStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("sent", announcementRepository.countByStatus(AnnouncementStatus.SENT));
        stats.put("draft", announcementRepository.countByStatus(AnnouncementStatus.DRAFT));
        stats.put("scheduled", announcementRepository.countByStatus(AnnouncementStatus.SCHEDULED));
        stats.put("cancelled", announcementRepository.countByStatus(AnnouncementStatus.CANCELLED));
        stats.put("hidden", announcementRepository.countByStatus(AnnouncementStatus.HIDDEN));
        return stats;
    }

    private void sendRealtimeNotification(AnnouncementEntity announcement, AnnouncementResponseDTO responseDTO) {
        // Gửi realtime dựa theo targetGroup
        if (announcement.getTargetGroup() == TargetGroup.ALL_USERS) {
            messagingTemplate.convertAndSend("/topic/announcements/ALL_USERS", responseDTO);
        } else if (announcement.getTargetGroup() == TargetGroup.LEARNERS_ONLY) {
            messagingTemplate.convertAndSend("/topic/announcements/LEARNERS_ONLY", responseDTO);
        } else if (announcement.getTargetGroup() == TargetGroup.MANAGERS_ONLY) {
            messagingTemplate.convertAndSend("/topic/announcements/MANAGERS_ONLY", responseDTO);
        }
    }

    // 30s kiểm tra 1 lần các thông báo đã lên lịch
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void processScheduledAnnouncements() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        List<AnnouncementEntity> scheduledList = announcementRepository
            .findByStatusAndScheduledTimeLessThanEqual(AnnouncementStatus.SCHEDULED, now);
        for (AnnouncementEntity entity : scheduledList) {
            entity.setStatus(AnnouncementStatus.SENT);
            entity.setSentTime(now);
            announcementRepository.save(entity);
            AnnouncementResponseDTO dto = announcementConverter.toDto(entity);
            sendRealtimeNotification(entity, dto);
        }
    }

    private static List<TargetGroup> getTargetGroups(UserEntity user) {
        List<TargetGroup> targetGroups = new ArrayList<>();
        targetGroups.add(TargetGroup.ALL_USERS);
        
        RoleEntity role = user.getRoleEntity();
        if (role != null) {
            String roleCode = role.getCode().toUpperCase();
            if ("LEARNER".equals(roleCode)) {
                targetGroups.add(TargetGroup.LEARNERS_ONLY);
            } else if ("MANAGER".equals(roleCode)) {
                targetGroups.add(TargetGroup.MANAGERS_ONLY);
            }
        }
        
        return targetGroups;
    }
}
