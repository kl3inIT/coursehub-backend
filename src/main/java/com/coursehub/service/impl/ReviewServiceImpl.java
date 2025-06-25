package com.coursehub.service.impl;

import com.coursehub.converter.ReviewConverter;
import com.coursehub.dto.request.review.ReviewRequestDTO;
import com.coursehub.dto.response.review.ReviewResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.ReviewEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.exceptions.course.CourseNotFoundException;
import com.coursehub.exceptions.review.ReviewAlreadyExistsException;
import com.coursehub.exceptions.review.ReviewNotFoundException;
import com.coursehub.exceptions.user.UserNotFoundException;
import com.coursehub.repository.CourseRepository;
import com.coursehub.repository.ReviewRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.NotificationService;
import com.coursehub.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ReviewConverter reviewConverter;
    private final NotificationService notificationService;

    @Override
    public Page<ReviewResponseDTO> findAllReviews(Long courseId, Long userId, Integer star, Pageable pageable) {
        Page<ReviewEntity> reviews = reviewRepository.findAllByFilters(courseId, userId, star, pageable);
        return reviews.map(reviewConverter::toResponseDTO);
    }

    @Override
    public ReviewResponseDTO findReviewById(Long id) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        return reviewConverter.toResponseDTO(review);
    }

    @Override
    @Transactional
    public ReviewResponseDTO createReview(Long userId, ReviewRequestDTO requestDTO) {
        // Check if user exists
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Check if course exists
        CourseEntity course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + requestDTO.getCourseId()));

        // Check if user has already reviewed this course
         if (reviewRepository.existsByUserEntityIdAndCourseEntityId(userId, requestDTO.getCourseId())) {
             throw new ReviewAlreadyExistsException("User has already reviewed this course");
         }

         // Check if user has purchased the course
         if (!course.getEnrollmentEntities().stream()
                 .anyMatch(enrollment -> enrollment.getUserEntity().getId().equals(userId))) {
             throw new IllegalStateException("User has not purchased this course");
         }

        // Create and save review
        ReviewEntity review = reviewConverter.toEntity(requestDTO);
        review.setUserEntity(user);
        review.setCourseEntity(course);

        ReviewEntity savedReview = reviewRepository.save(review);
        return reviewConverter.toResponseDTO(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO requestDTO) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));

        reviewConverter.updateEntity(review, requestDTO);
        ReviewEntity updatedReview = reviewRepository.save(review);
        return reviewConverter.toResponseDTO(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));
        reviewRepository.delete(review);

    }

    @Override
    public boolean existsByUserAndCourse(Long userId, Long courseId) {
        return reviewRepository.existsByUserEntityIdAndCourseEntityId(userId, courseId);
    }

    @Override
    public Double getAverageRating(Long courseId) {
        // Check if course exists
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException(courseId);
        }

        return reviewRepository.findByCourseEntityId(courseId)
                .stream()
                .mapToInt(ReviewEntity::getStar)
                .average()
                .orElse(0.0);
    }

    @Override
    public Long getTotalReviews(Long courseId) {
        // Check if course exists
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException(courseId);
        }

        return reviewRepository.countByCourseEntityId(courseId);
    }

    @Override
    public Long getTotalVisibleReviews() {
        return reviewRepository.count();
    }

    @Override
    public Double getOverallAverageRating() {
        double average = reviewRepository.findAll()
                .stream()
                .mapToInt(ReviewEntity::getStar)
                .average()
                .orElse(0.0);
        
        // Round to 2 decimal places
        return Math.round(average * 100.0) / 100.0;
    }

    @Override
    public Page<ReviewResponseDTO> findReviewsByVisibility(Integer visibilityStatus, Pageable pageable) {
        if (visibilityStatus != 0 && visibilityStatus != 1) {
            return new org.springframework.data.domain.PageImpl<>(new java.util.ArrayList<>(), pageable, 0);
        }
        
        Page<ReviewEntity> reviews = visibilityStatus == 0 
            ? reviewRepository.findVisibleReviews(pageable)
            : reviewRepository.findHiddenReviews(pageable);
            
        return reviews.map(reviewConverter::toResponseDTO);
    }

    @Override
    @Transactional
    public void setReviewVisibility(Long reviewId, boolean hide) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));
        review.setIsHidden(hide ? 1L : 0L);
        review.setModifiedDate(new Date());
        reviewRepository.save(review);
        if (hide) {
            notificationService.notifyHideResource(
                    review.getUserEntity().getId(),
                    reviewId,
                    "REVIEW"
            );
        } else {
            notificationService.notifyShowResource(
                    review.getUserEntity().getId(),
                    reviewId,
                    "REVIEW"
            );
        }
    }
} 