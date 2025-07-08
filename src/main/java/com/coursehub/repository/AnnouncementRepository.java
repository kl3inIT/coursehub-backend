package com.coursehub.repository;

import com.coursehub.entity.AnnouncementEntity;
import com.coursehub.enums.TargetGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long> {
    List<AnnouncementEntity> findByTargetGroupIn(List<TargetGroup> list);

}
