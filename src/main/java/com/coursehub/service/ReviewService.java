package com.coursehub.service;

import com.coursehub.dto.request.review.ReviewRequestDTO;
import com.coursehub.dto.response.review.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Page<ReviewResponseDTO> findAllReviews(Long courseId, Long userId, Integer star, Pageable pageable);

    ReviewResponseDTO findReviewById(Long id);

    ReviewResponseDTO createReview(Long userId, ReviewRequestDTO requestDTO);

    ReviewResponseDTO updateReview(Long id, ReviewRequestDTO requestDTO);

    void deleteReview(Long id);

    boolean existsByUserAndCourse(Long userId, Long courseId);

    Double getAverageRating(Long courseId);

    Long getTotalReviews(Long courseId);

    Long getTotalVisibleReviews();

    Double getOverallAverageRating();

    Page<ReviewResponseDTO> findReviewsByVisibility(Integer visibilityStatus, Pageable pageable);

    void setReviewVisibility(Long reviewId, boolean hide);
} 