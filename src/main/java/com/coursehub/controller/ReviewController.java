package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.review.ReviewRequestDTO;
import com.coursehub.dto.response.comment.CommentResponseDTO;
import com.coursehub.dto.response.review.ReviewResponseDTO;
import com.coursehub.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

import static com.coursehub.constant.Constant.CommonConstants.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ResponseGeneral<Page<ReviewResponseDTO>>> getAllReviews(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer star,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ReviewResponseDTO> reviewResponseDTOS = reviewService.findAllReviews(courseId, userId, star, pageRequest);

        ResponseGeneral<Page<ReviewResponseDTO>> response = new ResponseGeneral<>();
        response.setData(reviewResponseDTOS);
        response.setMessage(SUCCESS);
        response.setDetail("Reviews retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseGeneral<ReviewResponseDTO>> getReviewById(@PathVariable Long id) {
        ReviewResponseDTO review = reviewService.findReviewById(id);
        ResponseGeneral<ReviewResponseDTO> response = new ResponseGeneral<>();
        response.setData(review);
        response.setMessage(SUCCESS);
        response.setDetail("Review retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseGeneral<ReviewResponseDTO>> createReview(
            @Valid @RequestBody ReviewRequestDTO requestDTO, Principal principal) {

        ReviewResponseDTO review = reviewService.createReview(principal.getName(), requestDTO);
        ResponseGeneral<ReviewResponseDTO> response = new ResponseGeneral<>();
        response.setData(review);
        response.setMessage(SUCCESS);
        response.setDetail("Review created successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseGeneral<ReviewResponseDTO>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO requestDTO) {

        ReviewResponseDTO review = reviewService.updateReview(id, requestDTO);
        ResponseGeneral<ReviewResponseDTO> response = new ResponseGeneral<>();
        response.setData(review);
        response.setMessage(SUCCESS);
        response.setDetail("Review updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGeneral<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage(SUCCESS);
        response.setDetail("Review deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/check")
    public ResponseEntity<ResponseGeneral<Boolean>> checkUserReview(
            @RequestParam Long userId,
            @RequestParam Long courseId) {

        boolean exists = reviewService.existsByUserAndCourse(userId, courseId);
        ResponseGeneral<Boolean> response = new ResponseGeneral<>();
        response.setData(exists);
        response.setMessage(SUCCESS);
        response.setDetail(exists ? "Review exists for user and course" : "No review found for user and course");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/average-rating")
    public ResponseEntity<ResponseGeneral<Double>> getAverageRating(
            @RequestParam Long courseId) {

        Double averageRating = reviewService.getAverageRating(courseId);
        ResponseGeneral<Double> response = new ResponseGeneral<>();
        response.setData(averageRating);
        response.setMessage(SUCCESS);
        response.setDetail("Average rating retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/total-reviews")
    public ResponseEntity<ResponseGeneral<Long>> getTotalReviews(
            @RequestParam Long courseId) {

        Long totalReviews = reviewService.getTotalReviews(courseId);
        ResponseGeneral<Long> response = new ResponseGeneral<>();
        response.setData(totalReviews);
        response.setMessage(SUCCESS);
        response.setDetail("Total reviews retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{reviewId}/hide")
    public ResponseEntity<ResponseGeneral<String>> setReviewVisibility(
            @PathVariable Long reviewId,
            @RequestParam(required = false, defaultValue = "true") boolean hide) {
        reviewService.setReviewVisibility(reviewId, hide);
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage(hide ? "Review has been hidden" : "review has been shown");
        response.setData(hide ? "Hidden" : "Visible");
        return ResponseEntity.ok(response);
    }
} 