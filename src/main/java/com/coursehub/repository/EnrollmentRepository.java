package com.coursehub.repository;

import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.EnrollmentEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;




@Repository
public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity<?>, Long> {

    Long countByUserEntity_Id(Long userId);

    Long countByCourseEntity_Id(Long courseId);

    @Query("SELECT e FROM EnrollmentEntity e WHERE e.userEntity.id = :userId")
    Page<EnrollmentEntity> findEnrollmentsByUserId(@Param("userId") Long userId, Pageable pageable);
}
