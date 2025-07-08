package com.coursehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "announcement_user_read")
public class AnnouncementUserReadEntity extends  BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id")
    private AnnouncementEntity announcement;

    private Long userId;

    private Long isRead = 0L;

    private Long isDeleted = 0L;
}
