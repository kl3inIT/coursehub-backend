package com.coursehub.repository;

import com.coursehub.entity.CategoryDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryDiscountRepository extends JpaRepository<CategoryDiscountEntity, Long> {
    List<CategoryDiscountEntity> findByCategoryEntity_Id(Long categoryId);
}
