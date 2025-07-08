package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.contact.FeedbackRequestDTO;
import com.coursehub.dto.response.contact.FeedbackResponseDTO;
import com.coursehub.entity.FeedbackEntity;
import com.coursehub.service.FeedbackService;
import com.coursehub.service.UserService;
import com.coursehub.converter.FeedbackConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserService userService;
    private final FeedbackConverter feedbackConverter;

    @PostMapping
    public ResponseEntity<ResponseGeneral<String>> getFeedback(@RequestBody FeedbackRequestDTO dto) {
        Long userId = userService.getMyInfo().getId();
        feedbackService.submitFeedback(dto, userId);
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setData("OK");
        response.setMessage("Feedback submitted");
        response.setDetail("Feedback submitted");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllFeedback")
    public ResponseEntity<ResponseGeneral<List<FeedbackResponseDTO>>> getAllFeedback() {
        List<FeedbackResponseDTO> feedbackList = feedbackService.getAllFeedbacks();
        ResponseGeneral<List<FeedbackResponseDTO>> response = new ResponseGeneral<>();
        response.setData(feedbackList);
        response.setMessage("Feedback list retrieved");
        response.setDetail("All feedback retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<ResponseGeneral<String>> replyFeedback(@PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String reply = body.get("reply");
        feedbackService.replyFeedback(id, reply);
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setData("OK");
        response.setMessage("Reply sent and notification pushed!");
        response.setDetail("Reply sent and notification pushed!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseGeneral<FeedbackResponseDTO>> getFeedbackById(@PathVariable Long id) {
        FeedbackEntity feedback = feedbackService.getFeedbackById(id);
        FeedbackResponseDTO dto = feedbackConverter.toResponseDTO(feedback);
        ResponseGeneral<FeedbackResponseDTO> response = new ResponseGeneral<>();
        response.setData(dto);
        response.setMessage("Feedback detail retrieved");
        response.setDetail("Feedback detail retrieved successfully");
        return ResponseEntity.ok(response);
    }
}
