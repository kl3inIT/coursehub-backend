package com.coursehub.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.coursehub.constant.Constant.CommonConstants.ADMIN;
import static com.coursehub.constant.Constant.CommonConstants.COMMENT;
import static com.coursehub.constant.Constant.CommonConstants.REVIEW;

import com.coursehub.dto.response.notification.NotificationDTO;
import com.coursehub.entity.CommentEntity;
import com.coursehub.entity.NotificationEntity;
import com.coursehub.entity.ReviewEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.NotificationType;
import com.coursehub.enums.ReportStatus;
import com.coursehub.enums.ResourceType;
import com.coursehub.enums.UserStatus;
import com.coursehub.exceptions.user.UserNotFoundException;
import com.coursehub.repository.CommentRepository;
import com.coursehub.repository.NotificationRepository;
import com.coursehub.repository.ReviewRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    private void saveAndSendToUser(NotificationEntity notification, Long actorId, String actorName) {
        NotificationEntity saved = notificationRepository.save(notification);
        NotificationDTO dto = convertToDTO(saved, actorId, actorName);
        String email = notification.getUserEntity().getEmail();
        messagingTemplate.convertAndSendToUser(email, "/queue/notifications", dto);
    }

    @Override
    public void notifyLikeComment(Long userId, Long actorId, Long commentId) {
        UserEntity user = getUserById(userId);
        UserEntity actor = getUserById(actorId);
        NotificationEntity notification = new NotificationEntity();
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow();
        notification.setUserEntity(user);
        notification.setType(NotificationType.COMMENT_LIKED);
        notification.setMessage(actor.getName() + " liked your comment '" + comment.getComment() + "'.");
        notification.setIsRead(0L);
        notification.setResourceId(commentId);
        notification.setResourceType(COMMENT);
        notification.setLink(buildNotificationLink(comment, COMMENT));
        saveAndSendToUser(notification, actorId, actor.getName());
    }

    @Override
    public void notifyReplyComment(Long userId, Long actorId, Long commentId) {
        UserEntity user = getUserById(userId);
        UserEntity actor = getUserById(actorId);
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow();
        NotificationEntity notification = new NotificationEntity();
        notification.setUserEntity(user);
        notification.setType(NotificationType.COMMENT_REPLIED);
        notification.setMessage(actor.getName() + " replied to your comment '" +
                comment.getComment() + "'.");
        notification.setIsRead(0L);
        notification.setResourceId(commentId);
        notification.setResourceType(COMMENT);
        notification.setLink(buildNotificationLink(comment, COMMENT));
        saveAndSendToUser(notification, actorId, actor.getName());
    }

    @Override
    public void notifyHideResource(Long userId, Long resourceId, String resourceType) {
        UserEntity user = getUserById(userId);
        Object entity = getResourceEntity(resourceId, resourceType);
        NotificationEntity notification = buildResourceNotification(user, NotificationType.NOTIFICATION_HIDDEN, "hidden", entity, resourceType);
        saveAndSendToUser(notification, null, ADMIN);
    }

    @Override
    public void notifyShowResource(Long userId, Long resourceId, String resourceType) {
        UserEntity user = getUserById(userId);
        Object entity = getResourceEntity(resourceId, resourceType);
        NotificationEntity notification = buildResourceNotification(user, NotificationType.NOTIFICATION_SHOWN, "restored", entity, resourceType);
        saveAndSendToUser(notification, null, ADMIN);
    }

    private Object getResourceEntity(Long resourceId, String resourceType) {
        if (COMMENT.equalsIgnoreCase(resourceType)) {
            return commentRepository.findById(resourceId).orElseThrow();
        }
        if (REVIEW.equalsIgnoreCase(resourceType)) {
            return reviewRepository.findById(resourceId).orElseThrow();
        }
        throw new IllegalArgumentException("Invalid resourceType: " + resourceType);
    }

    @Override
    public void notifyBan(Long userId, String reason) {
        UserEntity user = getUserById(userId);
        NotificationEntity notification = new NotificationEntity();
        notification.setUserEntity(user);
        notification.setType(NotificationType.USER_BANNED);
        String message = "Your account has been banned by ADMIN for violating community guidelines.";
        if (reason != null && !reason.trim().isEmpty()) {
            message += " Reason: " + reason;
        }
        notification.setMessage(message);
        notification.setIsRead(0L);
        notification.setResourceId(null);
        notification.setResourceType(null);
        saveAndSendToUser(notification, null, ADMIN);
    }

    @Override
    public void notifyUnban(Long userId, String reason) {
        UserEntity user = getUserById(userId);
        NotificationEntity notification = new NotificationEntity();
        notification.setUserEntity(user);
        notification.setType(NotificationType.USER_UNBANNED);
        String message = "Your account has been restored by ADMIN.";
        if (reason != null && !reason.trim().isEmpty()) {
            message += " Reason: " + reason;
        }
        notification.setMessage(message);
        notification.setIsRead(0L);
        notification.setResourceId(null);
        notification.setResourceType(null);
        saveAndSendToUser(notification, null, ADMIN);
    }

    @Override
    public void notifyWarn(Long userId, Long resourceId, String resourceType) {
        UserEntity user = getUserById(userId);
        Object entity = getResourceEntity(resourceId, resourceType);
        String content = getResourceContent(entity, resourceType);
        String link = buildNotificationLink(entity, resourceType);

        NotificationEntity notification = new NotificationEntity();
        notification.setUserEntity(user);
        notification.setType(NotificationType.USER_WARNED);
        notification.setIsRead(0L);
        notification.setResourceId(resourceId);
        notification.setResourceType(resourceType);
        notification.setLink(link);

        if (COMMENT.equalsIgnoreCase(resourceType)) {
            notification.setMessage("You have been warned by ADMIN for violating community guidelines in your comment: '" + content + "'.");
        } else if (REVIEW.equalsIgnoreCase(resourceType)) {
            notification.setMessage("You have been warned by ADMIN for violating community guidelines in your review: '" + content + "'.");
        }

        saveAndSendToUser(notification, null, ADMIN);
    }

    @Override
    public void notifySystem(NotificationType type, String message) {
        List<UserEntity> users = userRepository.findAll();
        for (UserEntity user : users) {
            NotificationEntity notification = new NotificationEntity();
            notification.setUserEntity(user);
            notification.setType(type);
            notification.setMessage(message);
            notification.setIsRead(0L);
            notification.setResourceId(null);
            notification.setResourceType(null);
            NotificationEntity saved = notificationRepository.save(notification);
            NotificationDTO dto = convertToDTO(saved, null, null);
            messagingTemplate.convertAndSendToUser(user.getEmail(), "/queue/notifications", dto);
        }
    }

    @Override
    public void notifyReportStatusUpdate(Long reporterId, ResourceType resourceType, Long resourceId, ReportStatus status, String actionNote) {
        UserEntity reporter = getUserById(reporterId);
        if (reporter == null) {
            System.err.println("Reporter not found with ID: " + reporterId);
            return;
        }

        Object entity = getResourceEntity(resourceId, resourceType.name());
        String content = getResourceContent(entity, resourceType.name());
        String link = buildNotificationLink(entity, resourceType.name());

        NotificationEntity notification = new NotificationEntity();
        notification.setUserEntity(reporter);
        notification.setIsRead(0L);
        notification.setResourceId(resourceId);
        notification.setResourceType(resourceType.name());
        notification.setLink(link);

        String statusText = status == ReportStatus.APPROVED ? "approved" : "rejected";
        String message = String.format(
                "Your report about content '%s' has been %s by ADMIN. %s",
                content != null ? content.substring(0, Math.min(content.length(), 50)) + "..." : "content",
                statusText,
                actionNote != null && !actionNote.trim().isEmpty() ? "Reason: " + actionNote : ""
        );

        notification.setType(NotificationType.REPORT_PROCESSED);
        notification.setMessage(message);
        saveAndSendToUser(notification, null, ADMIN);
    }

    @Override
    public Page<NotificationDTO> getUserNotificationsByEmail(String email, Pageable pageable) {
        UserEntity user = userRepository.findByEmailAndIsActive(email, UserStatus.ACTIVE);
        if (user == null) throw new UserNotFoundException("User not found");
        return notificationRepository
                .findByUserEntity_IdOrderByCreatedDateDesc(user.getId(), pageable)
                .map(entity -> convertToDTO(entity, null, null));
    }

    @Override
    public Long getUnreadCount(String email) {
        UserEntity user = userRepository.findByEmailAndIsActive(email, UserStatus.ACTIVE);
        return notificationRepository.countUnreadByUserId(user.getId());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(1L);
            notificationRepository.save(notification);
        });
    }

    @Override
    @Transactional
    public void markAllAsRead(String email) {
        UserEntity user = userRepository.findByEmailAndIsActive(email, UserStatus.ACTIVE);
        notificationRepository.findByUserEntity_IdOrderByCreatedDateDesc(user.getId(), Pageable.unpaged()).forEach(notification -> {
            notification.setIsRead(1L);
            notificationRepository.save(notification);
        });
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notificationRepository::delete);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(String email) {
        UserEntity user = userRepository.findByEmailAndIsActive(email, UserStatus.ACTIVE);
        if (user != null) {
            notificationRepository.deleteByUserEntity_Id(user.getId());
        }
    }

    private NotificationDTO convertToDTO(NotificationEntity entity, Long actorId, String actorName) {
        return new NotificationDTO(entity.getId(), entity.getUserEntity().
                getId(), entity.getUserEntity().getName(), actorId, actorName, entity.getType(), entity.getMessage(),
                entity.getIsRead(), entity.getResourceId(), entity.getResourceType(), entity.getLink(), entity.getCreatedDate());
    }

    private UserEntity getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    private String buildNotificationLink(Object entity, String resourceType) {
        if (COMMENT.equalsIgnoreCase(resourceType) && entity instanceof CommentEntity comment) {
            return "/learn/" + comment.getLessonEntity().getModuleEntity().getCourseEntity().getId()
                    + "/" + comment.getLessonEntity().getId()
                    + "?commentId=" + comment.getId();
        }
        if (REVIEW.equalsIgnoreCase(resourceType) && entity instanceof ReviewEntity review) {
            return "/courses/" + review.getCourseEntity().getId() + "#review-" + review.getId();
        }
        return null;
    }

    private String getResourceContent(Object entity, String resourceType) {
        if (COMMENT.equalsIgnoreCase(resourceType) && entity instanceof CommentEntity comment) {
            return comment.getComment();
        }
        if (REVIEW.equalsIgnoreCase(resourceType) && entity instanceof ReviewEntity review) {
            return review.getComment();
        }
        return null;
    }

    private NotificationEntity buildResourceNotification(UserEntity user, NotificationType type, String action, Object entity, String resourceType) {
        NotificationEntity notification = new NotificationEntity();
        notification.setUserEntity(user);
        notification.setType(type);
        notification.setIsRead(0L);
        String content = getResourceContent(entity, resourceType);
        notification.setMessage("Your content '" + content + "' has been " + action + " by ADMIN.");
        notification.setResourceId(entity instanceof CommentEntity cmt ? cmt.getId() : ((ReviewEntity) entity).getId());
        notification.setResourceType(resourceType);
        notification.setLink(buildNotificationLink(entity, resourceType));
        return notification;
    }

}
