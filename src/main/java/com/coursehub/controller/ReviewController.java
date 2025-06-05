package com.coursehub.controller;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.dto.request.review.ReviewRequestDTO;
import com.coursehub.dto.response.category.CategoryResponseDTO;
import com.coursehub.dto.response.review.ReviewResponseDTO;
import com.coursehub.exception.review.ReviewNotFoundException;
import com.coursehub.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(defaultValue = "modifiedDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ReviewResponseDTO> reviewResponseDTOS = reviewService.findAllReviews(courseId, userId, star, pageRequest);

        ResponseGeneral<Page<ReviewResponseDTO>> response = new ResponseGeneral<>();
        response.setData(reviewResponseDTOS);

        if (reviewResponseDTOS.isEmpty()) {
            response.setMessage("No reviews found");
            response.setDetail("No reviews match the given criteria");
        } else {
            response.setMessage("Success");
            response.setDetail("Reviews retrieved successfully");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseGeneral<ReviewResponseDTO>> getReviewById(@PathVariable Long id) {
        ReviewResponseDTO review = reviewService.findReviewById(id);
        ResponseGeneral<ReviewResponseDTO> response = new ResponseGeneral<>();
        response.setData(review);
        response.setMessage("Success");
        response.setDetail("Review retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseGeneral<ReviewResponseDTO>> createReview(
            @RequestParam Long userId,
            @Valid @RequestBody ReviewRequestDTO requestDTO) {

        ReviewResponseDTO review = reviewService.createReview(userId, requestDTO);
        ResponseGeneral<ReviewResponseDTO> response = new ResponseGeneral<>();
        response.setData(review);
        response.setMessage("Success");
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
        response.setMessage("Success");
        response.setDetail("Review updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGeneral<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        response.setMessage("Success");
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
        response.setMessage("Success");
        response.setDetail(exists ? "Review exists for user and course" : "No review found for user and course");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 