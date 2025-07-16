package com.coursehub.service;

import com.coursehub.dto.request.contact.FeedbackRequestDTO;
import com.coursehub.dto.response.contact.FeedbackResponseDTO;
import com.coursehub.entity.FeedbackEntity;

import java.util.List;

public interface FeedbackService {

    void submitFeedback(FeedbackRequestDTO feedback, Long user_id);

    List<FeedbackResponseDTO> getAllFeedbacks();

    void replyFeedback(Long feedbackId, String reply);

    FeedbackEntity getFeedbackById(Long id);
}
