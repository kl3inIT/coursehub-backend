package com.coursehub.service.impl;

import com.coursehub.converter.FeedbackConverter;
import com.coursehub.dto.request.contact.FeedbackRequestDTO;
import com.coursehub.dto.response.contact.FeedbackResponseDTO;
import com.coursehub.entity.FeedbackEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.repository.FeedbackRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.repository.NotificationRepository;
import com.coursehub.entity.NotificationEntity;
import com.coursehub.enums.NotificationType;
import com.coursehub.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import com.coursehub.dto.response.notification.NotificationDTO;
import static com.coursehub.constant.Constant.CommonConstants.ADMIN;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final FeedbackConverter feedbackConverter;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void submitFeedback(FeedbackRequestDTO feedback, Long user_id) {
        log.info("Feedback submitted");
        UserEntity user = userRepository.findById(user_id).orElse(null);
        FeedbackEntity feedbackEntity = FeedbackEntity.builder()
                .userEntity(user)
                .fullName(feedback.getFullName())
                .email(feedback.getEmail())
                .category(feedback.getCategory())
                .subject(feedback.getSubject())
                .message(feedback.getMessage())
                .build();
        feedbackRepository.save(feedbackEntity);
    }

    @Override
    public List<FeedbackResponseDTO> getAllFeedbacks() {
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findAllByOrderByCreatedDateDesc();
        return feedbackConverter.toResponseDTO(feedbackEntities);
    }

    public void replyFeedback(Long feedbackId, String reply) {
        FeedbackEntity feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedback.setAdminReply(reply);
        feedbackRepository.save(feedback);
        // Tạo notification cho user
        NotificationEntity notification = new NotificationEntity();
        notification.setUserEntity(feedback.getUserEntity());
        notification.setType(NotificationType.FEEDBACK_REPLIED);
        notification.setMessage("Admin đã phản hồi feedback của bạn: " + reply);
        notification.setIsRead(0L);
        notification.setResourceId(feedback.getId());
        notification.setResourceType("FEEDBACK");
        notification.setLink("/user/feedback/" + feedback.getId());
        notificationRepository.save(notification);
        // Map sang NotificationDTO để gửi qua WebSocket
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(feedback.getUserEntity().getId());
        dto.setUserName(feedback.getUserEntity().getName());
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setResourceId(notification.getResourceId());
        dto.setResourceType(notification.getResourceType());
        dto.setLink(notification.getLink());
        dto.setCreatedAt(notification.getCreatedDate());

        String userTopic = "/topic/notifications/user-" + feedback.getUserEntity().getId();
        messagingTemplate.convertAndSend(userTopic, dto);
    }

    @Override
    public FeedbackEntity getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
    }
}
