package com.coursehub.repository;

import com.coursehub.entity.ReviewEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    @Query("SELECT r FROM ReviewEntity r WHERE " +
            "(:courseId IS NULL OR r.courseEntity.id = :courseId) AND " +
            "(:userId IS NULL OR r.userEntity.id = :userId) AND " +
            "(:star IS NULL OR r.star = :star)")
    Page<ReviewEntity> findAllByFilters(Long courseId, Long userId, Integer star, Pageable pageable);

    boolean existsByUserEntityIdAndCourseEntityId(Long userId, Long courseId);

    List<ReviewEntity> findByCourseEntityId(Long courseId);

    Long countByCourseEntityId(Long courseId);

    @Query("SELECT r FROM ReviewEntity r JOIN FETCH r.userEntity WHERE r.id = :id")
    Optional<ReviewEntity> findByIdWithUser(@Param("id") Long id);

    // New methods for visibility filtering
    @Query("SELECT r FROM ReviewEntity r WHERE r.isHidden IS NULL OR r.isHidden = 0")
    Page<ReviewEntity> findVisibleReviews(Pageable pageable);

    @Query("SELECT r FROM ReviewEntity r WHERE r.isHidden = 1")
    Page<ReviewEntity> findHiddenReviews(Pageable pageable);
} 