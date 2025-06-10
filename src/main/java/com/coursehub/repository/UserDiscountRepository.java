package com.coursehub.repository;

import com.coursehub.entity.UserDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDiscountRepository extends JpaRepository<UserDiscountEntity, Long> {
    UserDiscountEntity findByDiscountEntity_IdAndIsActive(Long discountId, Long isActive);
}
