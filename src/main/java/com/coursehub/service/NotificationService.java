package com.coursehub.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.coursehub.dto.response.notification.NotificationDTO;
import com.coursehub.enums.NotificationType;
import com.coursehub.enums.ReportStatus;
import com.coursehub.enums.ResourceType;

public interface NotificationService {

    Page<NotificationDTO> getUserNotificationsByEmail(String email, Pageable pageable);
    Long getUnreadCount(String email);
    void markAsRead(Long notificationId);
    void markAllAsRead(String email);
    void deleteNotification(Long notificationId);
    void deleteAllNotifications(String email);

    void notifyLikeComment(Long userId, Long actorId, Long commentId);
    void notifyReplyComment(Long userId, Long actorId, Long commentId);
    void notifyHideResource(Long userId, Long resourceId, String resourceType);
    void notifyShowResource(Long userId, Long resourceId, String resourceType);
    void notifyBan(Long userId, String reason);
    void notifyUnban(Long userId, String reason);
    void notifyWarn(Long userId, Long resourceId, String resourceType);
    void notifySystem(NotificationType type, String message);
    void notifyReportStatusUpdate(Long reporterId, ResourceType resourceType, Long resourceId, ReportStatus status, String actionNote);



}
