package com.coursehub.repository;

import com.coursehub.entity.DiscountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {
    @Query("""
                SELECT DISTINCT d FROM DiscountEntity d
                LEFT JOIN d.categoryDiscountEntities cde
                LEFT JOIN d.courseDiscountEntities cse
                WHERE (:isActive IS NULL OR d.isActive = :isActive)
                  AND (:categoryId IS NULL OR cde.categoryEntity.id = :categoryId)
                  AND (:courseId IS NULL OR cse.courseEntity.id = :courseId)
                  AND (:percentage IS NULL OR d.percentage >= :percentage)
                ORDER BY d.modifiedDate DESC
            """)
    Page<DiscountEntity> searchDiscounts(
            @Param("isActive") Long isActive,
            @Param("categoryId") Long categoryId,
            @Param("courseId") Long courseId,
            @Param("percentage") Double percentage,
            Pageable pageable);

    @Query("""
                SELECT DISTINCT d FROM DiscountEntity d
                LEFT JOIN d.categoryDiscountEntities cde
                LEFT JOIN d.courseDiscountEntities cse
                LEFT JOIN d.userDiscountEntities ude
                WHERE (:isActive IS NULL OR d.isActive = :isActive)
                  AND (:categoryId IS NULL OR cde.categoryEntity.id = :categoryId)
                  AND (:courseId IS NULL OR cse.courseEntity.id = :courseId)
                  AND (:userId IS NULL OR ude.userEntity.id = :userId)
                  AND (:percentage IS NULL OR d.percentage >= :percentage)
                  AND (d.endDate > :currentDateTime)
                ORDER BY d.modifiedDate DESC
            """)
    Page<DiscountEntity> searchDiscountsOwner(
            @Param("isActive") Long isActive,
            @Param("categoryId") Long categoryId,
            @Param("courseId") Long courseId,
            @Param("userId") Long userId,
            @Param("percentage") Double percentage,
            @Param("currentDateTime") java.time.LocalDateTime currentDateTime,
            Pageable pageable);
}
