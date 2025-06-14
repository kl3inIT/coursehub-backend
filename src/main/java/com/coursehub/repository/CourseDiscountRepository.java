package com.coursehub.repository;

import com.coursehub.entity.CourseDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseDiscountRepository extends JpaRepository<CourseDiscountEntity, Long> {
    List<CourseDiscountEntity> findByCourseEntity_Id(Long courseEntityId);
}
