package com.coursehub.repository;

import com.coursehub.entity.UserDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface UserDiscountRepository extends JpaRepository<UserDiscountEntity, Long> {
    UserDiscountEntity findByDiscountEntity_IdAndIsActive(Long discountId, Long isActive);

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
    List<UserDiscountEntity> findActiveDiscountsByUser(
            @Param("userId") Long userId,
            @Param("userIsActive") Long userIsActive,
            @Param("discountIsActive") Long discountIsActive,
            @Param("courseId") Long courseId,
            @Param("currentDateTime") LocalDateTime currentDateTime
    );
}
