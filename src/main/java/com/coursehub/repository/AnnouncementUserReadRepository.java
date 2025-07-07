package com.coursehub.repository;

import com.coursehub.entity.AnnouncementUserReadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementUserReadRepository extends JpaRepository<AnnouncementUserReadEntity, Long> {
    boolean existsByAnnouncementIdAndUserId(Long announcementId, Long userId);
    AnnouncementUserReadEntity findByAnnouncementIdAndUserId(Long announcementId, Long userId);
}
