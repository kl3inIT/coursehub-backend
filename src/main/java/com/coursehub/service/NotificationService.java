package com.coursehub.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.coursehub.dto.response.notification.NotificationDTO;
import com.coursehub.enums.NotificationType;

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
    void notifyBan(Long userId);
    void notifyUnban(Long userId);
    void notifyWarn(Long userId, Long resourceId, String resourceType);
    void notifySystem(NotificationType type, String message);



}
