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
        if (review == null) {
            response.setMessage("Review not found");
            response.setDetail("No review found with the given ID");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.setData(review);
        response.setMessage("Success");
        response.setDetail("Review retrieved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @PostMapping
//    public ResponseEntity<ResponseGeneral<ReviewResponseDTO>> createReview(
//            @RequestParam Long userId,
//            @Valid @RequestBody ReviewRequestDTO requestDTO) {
//
//        ResponseGeneral<ReviewResponseDTO> response = new ResponseGeneral<>();
//
//        try {
//            ReviewResponseDTO review = reviewService.createReview(userId, requestDTO);
//            response.setData(review);
//            response.setMessage("Success");
//            response.setDetail("Review created successfully");
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception e) {
//            response.setMessage("Create review failed");
//            response.setDetail(e.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ResponseGeneral<ReviewResponseDTO>> updateReview(
//            @PathVariable Long id,
//            @Valid @RequestBody ReviewRequestDTO requestDTO) {
//
//        ResponseGeneral<ReviewResponseDTO> response = new ResponseGeneral<>();
//
//        try {
//            ReviewResponseDTO review = reviewService.updateReview(id, requestDTO);
//            response.setData(review);
//            response.setMessage("Success");
//            response.setDetail("Review updated successfully");
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception e) {
//            response.setMessage("Update failed");
//            response.setDetail(e.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGeneral<Void>> deleteReview(@PathVariable Long id) {
        ResponseGeneral<Void> response = new ResponseGeneral<>();
        try {
            reviewService.deleteReview(id);
            response.setMessage("Success");
            response.setDetail("Review deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ReviewNotFoundException e) {
            response.setMessage("Delete failed");
            response.setDetail(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.setMessage("Delete failed");
            response.setDetail("An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<ResponseGeneral<Boolean>> checkUserReview(
            @RequestParam Long userId,
            @RequestParam Long courseId) {

        ResponseGeneral<Boolean> response = new ResponseGeneral<>();

        try {
            boolean exists = reviewService.existsByUserAndCourse(userId, courseId);
            response.setData(exists);
            response.setMessage(exists ? "User has already reviewed this course" : "User has not reviewed this course");
            response.setDetail(exists ? "Review exists for user and course" : "No review found for user and course");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("Check failed");
            response.setDetail(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
} 