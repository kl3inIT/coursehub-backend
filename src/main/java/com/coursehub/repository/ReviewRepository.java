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

    // New method for advanced filtering with visibility, star, category, course and search
    @Query("SELECT r FROM ReviewEntity r " +
           "JOIN r.courseEntity c " +
           "WHERE ((:visibilityStatus = 0 AND (r.isHidden IS NULL OR r.isHidden = 0)) OR " +
           "       (:visibilityStatus = 1 AND r.isHidden = 1)) AND " +
           "(:star IS NULL OR r.star = :star) AND " +
           "(:categoryId IS NULL OR c.categoryEntity.id = :categoryId) AND " +
           "(:courseId IS NULL OR r.courseEntity.id = :courseId) AND " +
           "(:search IS NULL OR :search = '' OR LOWER(r.comment) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ReviewEntity> findByVisibilityWithFilters(
            @Param("visibilityStatus") Integer visibilityStatus,
            @Param("star") Integer star,
            @Param("categoryId") Long categoryId,
            @Param("courseId") Long courseId,
            @Param("search") String search,
            Pageable pageable);
    Optional<ReviewEntity> findByCourseEntityIdAndUserEntityId(Long courseId, Long userId);
}