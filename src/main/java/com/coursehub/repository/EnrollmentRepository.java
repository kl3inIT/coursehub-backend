package com.coursehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coursehub.entity.EnrollmentEntity;


@Repository
public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {

    Long countByUserEntity_Id(Long userId);

    Long countByCourseEntity_Id(Long courseId);

    List<EnrollmentEntity> findEnrollmentEntitiesByUserEntity_Id(Long userId);

    EnrollmentEntity findByUserEntity_IdAndCourseEntity_Id(Long userEntityId, Long courseEntityId);
    
    List<EnrollmentEntity> findByCourseEntity_IdIn(List<Long> courseIds);

}
