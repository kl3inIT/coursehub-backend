package com.coursehub.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coursehub.entity.CourseEntity;
import com.coursehub.enums.CourseStatus;
import java.util.Date;

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

    @Query("SELECT COUNT(c) FROM CourseEntity c WHERE c.createdDate BETWEEN :startDate AND :endDate")
    Long countCoursesByCreatedAtBetween(@Param("startDate") Date startDate,
                                        @Param("endDate") Date endDate);

    @Query("SELECT MONTH(c.createdDate) as month, COUNT(c.id) as total FROM CourseEntity c WHERE YEAR(c.createdDate) = :year GROUP BY MONTH(c.createdDate)")
    List<Object[]> countNewCoursesByMonth(@Param("year") int year);
}
