package com.coursehub.dto.request.announcement;

import java.time.LocalDateTime;
import java.util.List;

import com.coursehub.enums.AnnouncementStatus;
import com.coursehub.enums.AnnouncementType;
import com.coursehub.enums.TargetGroup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementCreateRequestDTO {
    private String title;
    private String content;
    private AnnouncementType type;
    private TargetGroup targetGroup;
    private AnnouncementStatus status;
    private String link;
    private LocalDateTime scheduledTime;
    private List<Long> courseIds;
    private List<Long> userIds;
}
