package com.coursehub.repository;

import com.coursehub.entity.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {
    DiscountEntity findByCodeAndIsActive(String code, Long isActive);


}
