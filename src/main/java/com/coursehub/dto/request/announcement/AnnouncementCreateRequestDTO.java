package com.coursehub.dto.request.announcement;

import java.time.LocalDateTime;
import java.util.List;

import com.coursehub.enums.AnnouncementStatus;
import com.coursehub.enums.AnnouncementType;
import com.coursehub.enums.TargetGroup;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementCreateRequestDTO {

    @Size(min = 1, max = 500, message = "Title must be between 1 and 500 characters")
    private String title;

    @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters")
    private String content;
    private AnnouncementType type;
    private TargetGroup targetGroup;
    private AnnouncementStatus status;

    @Size(max = 500, message = "Link must not exceed 500 characters")
    private String link;
    private LocalDateTime scheduledTime;
    private List<Long> courseIds;
    private List<Long> userIds;
}
