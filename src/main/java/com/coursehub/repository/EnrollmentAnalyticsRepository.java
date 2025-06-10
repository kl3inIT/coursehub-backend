package com.coursehub.repository;

import com.coursehub.entity.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EnrollmentAnalyticsRepository extends JpaRepository<EnrollmentEntity, Long> {
    
    @Query("SELECT COUNT(e) FROM EnrollmentEntity e WHERE e.courseEntity.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT e.courseEntity.id, COUNT(e) FROM EnrollmentEntity e GROUP BY e.courseEntity.id")
    List<Object[]> countEnrollmentsByAllCourses();
}
