package com.coursehub.repository;

import com.coursehub.entity.CategoryDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDiscountRepository extends JpaRepository<CategoryDiscountEntity, Long> {
    CategoryDiscountEntity findByCategoryEntity_Id(Long categoryId);
}
