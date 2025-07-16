package com.coursehub.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.coursehub.enums.AnnouncementStatus;
import com.coursehub.enums.AnnouncementType;
import com.coursehub.enums.TargetGroup;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "announcements")
public class AnnouncementEntity extends BaseEntity{
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private AnnouncementType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TargetGroup targetGroup;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AnnouncementStatus status = AnnouncementStatus.DRAFT;

    private String link;

    private LocalDateTime scheduledTime;

    private LocalDateTime sentTime;

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnouncementUserReadEntity> userReads;

    private Long createdBy;

    private Long isDeleted = 0L;

    @Column(columnDefinition = "TEXT")
    private String targetCourseIds;

    @Column(columnDefinition = "TEXT")
    private String targetUserIds;
}
