package com.coursehub.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coursehub.entity.CourseEntity;
import com.coursehub.enums.CourseStatus;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    @Query("""
            SELECT c
            FROM CourseEntity c
            LEFT JOIN c.enrollmentEntities e
            WHERE c.status = :status
            GROUP BY c.id
            ORDER BY COUNT(e.id) DESC
            """)
    List<CourseEntity> findFeaturedCourse(@Param("status") CourseStatus status, Pageable pageable);

    List<CourseEntity> findAllByStatus(CourseStatus status);
    
    List<CourseEntity> findByStatus(CourseStatus status);

    List<CourseEntity> findByCategoryEntity_Id(Long categoryId);

    @Query(value = "SELECT * FROM courses ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<CourseEntity> getCoursesRecommend();
}
