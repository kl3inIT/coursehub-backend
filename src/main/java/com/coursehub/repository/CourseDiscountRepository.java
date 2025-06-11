package com.coursehub.repository;

import com.coursehub.entity.CourseDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseDiscountRepository extends JpaRepository<CourseDiscountEntity, Long> {
    List<CourseDiscountEntity> findByCourseEntity_Id(Long courseEntityId);
}
