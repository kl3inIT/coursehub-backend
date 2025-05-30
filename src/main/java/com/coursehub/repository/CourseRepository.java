package com.coursehub.repository;

import com.coursehub.entity.CategoryEntity;
import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.EnrollmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    @Query("SELECT c FROM CourseEntity c LEFT JOIN c.enrollmentEntities e GROUP BY c.id ORDER BY COUNT(e.id) DESC")
    List<CourseEntity> findFeaturedCourse(Pageable pageable);

    Page<CourseEntity> findAll(Pageable pageable);

    List<CourseEntity> findByCategoryEntity_Id(Long categoryId);
}
