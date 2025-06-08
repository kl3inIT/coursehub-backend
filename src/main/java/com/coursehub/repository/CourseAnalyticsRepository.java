package com.coursehub.repository;

import com.coursehub.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CourseAnalyticsRepository extends JpaRepository<CourseEntity, Long> {
    
    @Query("SELECT c FROM CourseEntity c WHERE c.id = :courseId")
    CourseEntity findCourseById(@Param("courseId") Long courseId);

    @Query("SELECT c FROM CourseEntity c")
    List<CourseEntity> findAllCourses();

    @Query("SELECT c.id as courseId, COUNT(e) as enrollmentCount " +
           "FROM CourseEntity c LEFT JOIN c.enrollmentEntities e " +
           "WHERE c.id = :courseId " +
           "GROUP BY c.id")
    Map<String, Long> getEnrollmentStats(@Param("courseId") Long courseId);

    // TODO: Implement after adding status field to EnrollmentEntity
    /*
    @Query("SELECT c.id as courseId, COUNT(e) as activeCount " +
           "FROM CourseEntity c LEFT JOIN c.enrollmentEntities e " +
           "WHERE c.id = :courseId AND e.status = 'ACTIVE' " +
           "GROUP BY c.id")
    Map<String, Long> getActiveStudentsCount(@Param("courseId") Long courseId);
    
    @Query("SELECT c.id as courseId, COUNT(e) as completedCount " +
           "FROM CourseEntity c LEFT JOIN c.enrollmentEntities e " +
           "WHERE c.id = :courseId AND e.status = 'COMPLETED' " +
           "GROUP BY c.id")
    Map<String, Long> getCompletedStudentsCount(@Param("courseId") Long courseId);
    */
} 