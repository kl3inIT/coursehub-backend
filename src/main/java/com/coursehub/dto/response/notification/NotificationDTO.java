package com.coursehub.dto.response.notification;

import java.util.Date;

import com.coursehub.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long actorId;
    private String actorName;
    private NotificationType type;
    private String message;
    private Long isRead;
    private Long resourceId;
    private String resourceType;
    private String link;
    private Date createdAt;
} 