package com.coursehub.repository;

import com.coursehub.entity.UserDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserDiscountRepository extends JpaRepository<UserDiscountEntity, Long> {
    long countByIsActive(Long isActive);

    UserDiscountEntity findByDiscountEntity_IdAndIsActiveAndUserEntity_Id(Long discountId, Long isActive, Long userId);

    UserDiscountEntity findByUserEntity_IdAndDiscountEntity_Id(Long userId, Long discountId);

    @Query("""
                SELECT ude FROM UserDiscountEntity ude
                LEFT JOIN ude.discountEntity.courseDiscountEntities cde
                WHERE ude.userEntity.id = :userId
                  AND ude.isActive = :userIsActive
                  AND ude.discountEntity.isActive = :discountIsActive
                  AND ude.discountEntity.endDate > :currentDateTime
                  AND ude.discountEntity.startDate < :currentDateTime
                  AND (:courseId IS NULL OR cde.courseEntity.id = :courseId)
            """)
    List<UserDiscountEntity> findActiveDiscountsByCourseId(
            @Param("userId") Long userId,
            @Param("userIsActive") Long userIsActive,
            @Param("discountIsActive") Long discountIsActive,
            @Param("courseId") Long courseId,
            @Param("currentDateTime") LocalDateTime currentDateTime
    );


}
