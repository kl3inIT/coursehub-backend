package com.coursehub.service.impl;

import com.coursehub.converter.ReviewConverter;
import com.coursehub.dto.request.review.ReviewRequestDTO;
import com.coursehub.dto.response.review.ReviewResponseDTO;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.ReviewEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.exceptions.course.CourseNotFoundException;
import com.coursehub.exceptions.review.ReviewNotFoundException;
import com.coursehub.exceptions.user.UserNotFoundException;
import com.coursehub.repository.CourseRepository;
import com.coursehub.repository.ReviewRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ReviewConverter reviewConverter;

    @Override
    public Page<ReviewResponseDTO> findAllReviews(Long courseId, Long userId, Integer star, Pageable pageable) {
        Page<ReviewEntity> reviews = reviewRepository.findAllByFilters(courseId, userId, star, pageable);
        return reviews.map(reviewConverter::toResponseDTO);
    }

    @Override
    public ReviewResponseDTO findReviewById(Long id) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));
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
        // if (reviewRepository.existsByUserEntityIdAndCourseEntityId(userId, requestDTO.getCourseId())) {
        //     throw new ReviewAlreadyExistsException("User has already reviewed this course");
        // }

        // // Check if user has purchased the course
        // if (!course.getEnrollmentEntities().stream()
        //         .anyMatch(enrollment -> enrollment.getUserEntity().getId().equals(userId))) {
        //     throw new IllegalStateException("User has not purchased this course");
        // }

        // Create and save review
        ReviewEntity review = reviewConverter.toEntity(requestDTO);
        review.setUserEntity(user);
        review.setCourseEntity(course);

        try {
            ReviewEntity savedReview = reviewRepository.saveAndFlush(review);
            return reviewConverter.toResponseDTO(savedReview);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create review: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO requestDTO) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));

        reviewConverter.updateEntity(review, requestDTO);
        try {
            ReviewEntity updatedReview = reviewRepository.save(review);
            return reviewConverter.toResponseDTO(updatedReview);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update review: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));

        try {
            reviewRepository.delete(review);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete review: " + e.getMessage());
        }
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
} 