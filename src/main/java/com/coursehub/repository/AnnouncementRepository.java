package com.coursehub.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coursehub.entity.AnnouncementEntity;
import com.coursehub.enums.AnnouncementStatus;
import com.coursehub.enums.AnnouncementType;
import com.coursehub.enums.TargetGroup;

public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long> {
    List<AnnouncementEntity> findByTargetGroupInAndStatus(List<TargetGroup> list, AnnouncementStatus status);
    List<AnnouncementEntity> findByStatusAndScheduledTimeLessThanEqual(AnnouncementStatus status, LocalDateTime time);

    @Query("""
        SELECT a FROM AnnouncementEntity a
        WHERE (:type IS NULL OR a.type = :type)
          AND (:targetGroup IS NULL OR a.targetGroup = :targetGroup)
          AND (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:statuses IS NULL OR a.status IN :statuses)
          AND (:start IS NULL OR a.createdDate >= :start)
          AND (:end IS NULL OR a.createdDate <= :end)
        """)
    Page<AnnouncementEntity> filterAnnouncements(
        @Param("type") AnnouncementType type,
        @Param("statuses") List<AnnouncementStatus> statuses,
        @Param("targetGroup") TargetGroup targetGroup,
        @Param("search") String search,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        Pageable pageable
    );

    int countByStatus(AnnouncementStatus status);
}

