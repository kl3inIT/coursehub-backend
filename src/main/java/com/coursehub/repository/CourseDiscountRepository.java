package com.coursehub.repository;

import com.coursehub.entity.CourseDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseDiscountRepository extends JpaRepository<CourseDiscountEntity, Long> {
    CourseDiscountEntity findByCourseEntity_Id(Long courseEntityId);
}
