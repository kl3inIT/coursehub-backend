package com.coursehub.repository;

import com.coursehub.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    /**
     * Find course by ID with all relationships fetched
     * @param courseId The ID of the course
     * @return Optional containing the course with relationships
     */
    @Query("SELECT DISTINCT c FROM CourseEntity c " +
           "LEFT JOIN FETCH c.user " +
           "LEFT JOIN FETCH c.reviews " +
           "LEFT JOIN FETCH c.enrollments " +
           "LEFT JOIN FETCH c.lessons " +
           "WHERE c.id = :courseId")
    Optional<CourseEntity> findByIdWithDetails(@Param("courseId") Long courseId);

    /**
     * Find featured courses (top 4 active courses with highest ratings)
     * @return List of top 4 featured courses
     */
    @Query("SELECT c FROM CourseEntity c WHERE c.isActive = true")
    List<CourseEntity> findFeaturedCourses();

}
