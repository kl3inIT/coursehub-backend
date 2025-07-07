package com.coursehub.entity;

import com.coursehub.enums.NotificationType;
import com.coursehub.enums.TargetGroup;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "announcements")
public class AnnouncementEntity extends BaseEntity{
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private TargetGroup targetGroup;

    private String link;

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnnouncementUserReadEntity> userReads;
}
