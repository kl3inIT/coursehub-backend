package com.coursehub.repository;

import com.coursehub.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    
    @Query("SELECT r FROM ReviewEntity r WHERE r.isActive = true AND " +
           "(:courseId IS NULL OR r.courseEntity.id = :courseId) AND " +
           "(:userId IS NULL OR r.userEntity.id = :userId) AND " +
           "(:star IS NULL OR r.star = :star)")
    Page<ReviewEntity> findAllByFilters(Long courseId, Long userId, Integer star, Pageable pageable);

    boolean existsByUserEntityIdAndCourseEntityIdAndIsActiveTrue(Long userId, Long courseId);
} 