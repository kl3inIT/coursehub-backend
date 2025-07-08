package com.coursehub.service;

import com.coursehub.dto.request.announcement.AnnouncementCreateRequestDTO;
import com.coursehub.dto.response.announcement.AnnouncementResponseDTO;
import com.coursehub.dto.response.announcement.TargetGroupDTO;
import com.coursehub.enums.TargetGroup;

import java.util.List;

public interface AnnouncementService {
    AnnouncementResponseDTO createAnnouncement(AnnouncementCreateRequestDTO dto);
    List<AnnouncementResponseDTO> getAnnouncements(TargetGroup targetGroup);
    AnnouncementResponseDTO getAnnouncement(Long id);
    void deleteAnnouncement(Long id);
    void markAsRead(Long announcementId);
    List<TargetGroupDTO> getAllTargetGroups();
    void markAsDeleted(Long announcementId);
    List<AnnouncementResponseDTO> getAnnouncementsForCurrentUser();
}