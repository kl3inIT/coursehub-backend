package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.coursehub.dto.response.notification.NotificationDTO;
import com.coursehub.enums.NotificationType;
import com.coursehub.service.NotificationService;

import lombok.RequiredArgsConstructor;

import static com.coursehub.constant.Constant.CommonConstants.SUCCESS;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ResponseGeneral<Page<NotificationDTO>>> getUserNotifications(
            Authentication authentication,
            Pageable pageable) {
        ResponseGeneral<Page<NotificationDTO>> response = new ResponseGeneral<>();
        response.setMessage(SUCCESS);
        String email = authentication.getName();
        response.setData(notificationService.getUserNotificationsByEmail(email, pageable));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ResponseGeneral<Long>> getUnreadCount(Authentication authentication) {
        ResponseGeneral<Long> response = new ResponseGeneral<>();
        response.setMessage(SUCCESS);
        String email = authentication.getName();
        response.setData(notificationService.getUnreadCount(email));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<ResponseGeneral<Void>> markAsRead(@PathVariable Long notificationId) {
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage(SUCCESS);
        notificationService.markAsRead(notificationId);
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/read-all")
    public ResponseEntity<ResponseGeneral<Void>> markAllAsRead(Authentication authentication) {
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage(SUCCESS);
        String email = authentication.getName();
        notificationService.markAllAsRead(email);
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseGeneral<Void>> sendSystemNotification(
            @RequestParam NotificationType type,
            @RequestParam String message) {
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage(SUCCESS);
        notificationService.notifySystem(type, message);
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ResponseGeneral<Void>> deleteNotification(@PathVariable Long notificationId) {
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage(SUCCESS);
        notificationService.deleteNotification(notificationId);
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/all")
    public ResponseEntity<ResponseGeneral<Void>> deleteAllNotifications(Authentication authentication) {
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage(SUCCESS);
        String email = authentication.getName();
        notificationService.deleteAllNotifications(email);
        response.setData(null);
        return ResponseEntity.ok(response);
    }
} 