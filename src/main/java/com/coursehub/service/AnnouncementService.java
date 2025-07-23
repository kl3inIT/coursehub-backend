package com.coursehub.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.coursehub.dto.request.announcement.AnnouncementCreateRequestDTO;
import com.coursehub.dto.response.announcement.AnnouncementResponseDTO;
import com.coursehub.dto.response.announcement.TargetGroupDTO;
import com.coursehub.enums.AnnouncementStatus;
import com.coursehub.enums.AnnouncementType;
import com.coursehub.enums.TargetGroup;

public interface AnnouncementService {
    AnnouncementResponseDTO createAnnouncement(AnnouncementCreateRequestDTO dto);
    Page<AnnouncementResponseDTO> getAllAnnouncements(
        AnnouncementType type,
        List<AnnouncementStatus> statuses,
        TargetGroup targetGroup,
        String search,
        Pageable pageable
    );
    AnnouncementResponseDTO getAnnouncement(Long id);
    AnnouncementResponseDTO updateAnnouncement(Long id, AnnouncementCreateRequestDTO dto);
    void deleteAnnouncement(Long id);
    AnnouncementResponseDTO sendAnnouncement(Long id);
    AnnouncementResponseDTO scheduleAnnouncement(Long id, String scheduledTime);
    AnnouncementResponseDTO saveAsDraft(Long id);
    AnnouncementResponseDTO cancelAnnouncement(Long id);
    void markAsRead(Long announcementId);
    List<TargetGroupDTO> getAllTargetGroups();
    void markAsDeleted(Long announcementId);
    void restoreAnnouncement(Long announcementId);
    void archiveAnnouncement(Long announcementId);
    List<AnnouncementResponseDTO> getAnnouncementsForCurrentUser();
    Map<String, Integer> getAnnouncementStats();
    AnnouncementResponseDTO cloneAnnouncement(Long id);
}