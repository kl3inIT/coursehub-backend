package com.coursehub.dto.response.announcement;

import com.coursehub.enums.NotificationType;
import com.coursehub.enums.TargetGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponseDTO {
    private Long id;
    private String title;
    private String content;
    private NotificationType type;
    private TargetGroup targetGroup;
    private String targetGroupDescription;
    private String link;
    private String createdAt;
    private Long isRead;
    private Long isDeleted;
}
