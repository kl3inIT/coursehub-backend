package com.coursehub.repository;

import com.coursehub.entity.UserDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDiscountRepository extends JpaRepository<UserDiscountEntity, Long> {
    UserDiscountEntity findByDiscountEntity_IdAndIsActive(Long discountId, Long isActive);
    UserDiscountEntity findByUserEntity_IdAndDiscountEntity_Id(Long userId, Long discountId);
    UserDiscountEntity findByUserEntity_IdAndDiscountEntity_IdAndIsActive(Long userId, Long discountId, Long isActive);
}
