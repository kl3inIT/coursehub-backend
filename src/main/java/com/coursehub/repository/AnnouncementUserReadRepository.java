package com.coursehub.repository;

import com.coursehub.entity.AnnouncementUserReadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnnouncementUserReadRepository extends JpaRepository<AnnouncementUserReadEntity, Long> {
    boolean existsByAnnouncementIdAndUserId(Long announcementId, Long userId);
    Optional<AnnouncementUserReadEntity> findByAnnouncementIdAndUserId(Long announcementId, Long userId);
    List<AnnouncementUserReadEntity> findByUserId(Long userId);
}
