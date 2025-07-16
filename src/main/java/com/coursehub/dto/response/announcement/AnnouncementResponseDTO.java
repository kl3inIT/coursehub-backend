package com.coursehub.dto.response.announcement;

import java.time.LocalDateTime;
import java.util.Date;

import com.coursehub.enums.AnnouncementStatus;
import com.coursehub.enums.AnnouncementType;
import com.coursehub.enums.TargetGroup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnnouncementResponseDTO {
    private Long id;
    private String title;
    private String content;
    private AnnouncementType type;
    private TargetGroup targetGroup;
    private String targetGroupDescription;
    private AnnouncementStatus status;
    private String link;
    private LocalDateTime scheduledTime;
    private LocalDateTime sentTime;
    private Date createdAt;
    private Long isRead;
    private String createdByName;
}
